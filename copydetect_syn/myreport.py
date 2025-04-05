import logging
from jinja2 import Template
import numpy as np
import matplotlib.pyplot as plt
import io
import os
import base64
import json
import webbrowser
from pathlib import Path
from tqdm import tqdm
import time
import warnings
from markupsafe import escape
import defaults
from _config import CopydetectConfig
import os
current_dir = os.path.dirname(os.path.abspath(__file__))

class CodeFingerprint:
   
    def __init__(self, file, k, win_size, boilerplate=None, filter=True,
                 language=None, fp=None, encoding: str = "utf-8"):
        if boilerplate is None:
            boilerplate = []
        if fp is not None:
            code = fp.read()
        elif encoding == "DETECT":
            try:
                import chardet
                with open(file, "rb") as code_fp:
                    code = code_fp.read()
                detected_encoding = chardet.detect(code)["encoding"]
                if detected_encoding is not None:
                    code = code.decode(detected_encoding)
                else:
                    # if encoding can't be detected, just use the default
                    # encoding (the file may be empty)
                    code = code.decode()
            except ModuleNotFoundError as e:
                logging.error(
                    "encoding detection requires chardet to be installed"
                )
                raise e
        else:
            with open(file, encoding=encoding) as code_fp:
                code = code_fp.read()

        self.filename = file
        self.raw_code = code
        self.k = k
        
class Report:
 
    def __init__(self, test_dirs=None, ref_dirs=None,
                 boilerplate_dirs=None, extensions=None,
                 noise_t=defaults.NOISE_THRESHOLD,
                 guarantee_t=defaults.GUARANTEE_THRESHOLD,
                 display_t=defaults.DISPLAY_THRESHOLD,
                 same_name_only=False, ignore_leaf=False, autoopen=True,
                 disable_filtering=False, force_language=None,
                 truncate=False, out_file="./report.html", silent=False,
                 encoding: str = "utf-8"):
        conf_args = locals()
        conf_args = {
            key: val
            for key, val in conf_args.items()
            if key != "self" and val is not None
        }
        self.conf = CopydetectConfig(**conf_args)
        self.test_files = self._get_file_list(
            self.conf.test_dirs, self.conf.extensions
        )
        self.ref_files = self._get_file_list(
            self.conf.ref_dirs, self.conf.extensions
        )
        self.boilerplate_files = self._get_file_list(
            self.conf.boilerplate_dirs, self.conf.extensions
        )
        # before run() is called, similarity data should be empty
        self.similarity_matrix = np.array([])
        self.token_overlap_matrix = np.array([])
        self.slice_matrix = {}
        self.file_data = {}

    def _preprocess_code(self, file_list):
        for code_f in tqdm(file_list, bar_format= '   {l_bar}{bar}{r_bar}',
                           disable=self.conf.silent):
            if code_f not in self.file_data:
                try:
                    self.file_data[code_f] = CodeFingerprint(
                        code_f, self.conf.noise_t, self.conf.window_size,
                        None, not self.conf.disable_filtering,
                        self.conf.force_language, encoding=self.conf.encoding)

                except UnicodeDecodeError:
                    logging.warning(f"Skipping {code_f}: file not UTF-8 text")
                    continue


    
    @classmethod
    def from_config(cls, config):
        params = CopydetectConfig.normalize_json(config)
        return cls(**params)

    def _get_file_list(self, dirs, exts):
        file_list = []
        for dir in dirs:
            print_warning = True
            for ext in exts:
                if ext == "*":
                    matched_contents = Path(dir).rglob("*")
                else:
                    matched_contents = Path(dir).rglob("*." + ext.lstrip("."))
                files = [str(f) for f in matched_contents if f.is_file()]

                if len(files) > 0:
                    print_warning = False
                file_list.extend(files)
            if print_warning:
                logging.warning("No files found in " + dir)

        # convert to a set to remove duplicates, then back to a list
        return list(set(file_list))
        
        
        # 改4
    def get_copied_slices(self, features: dict, feature_name: str):
            slices1_offset = []
            slices2_offset = []

            slices1_length = []
            slices2_length = []

            fe = features[feature_name]
            # print(f"[DEBUG] Checking feature: {feature_name}")
            for vv in fe:
                v = fe[vv]
                # print(f"[PAIR] Suspicious: offset={v['offset']}, length={v['length']} | "
                #     f"Source: offset={v['srcOffset']}, length={v['srcLength']}")

                slices1_offset.append(v['offset'])
                slices1_length.append(v['length'])

                slices2_offset.append(v['srcOffset'])
                slices2_length.append(v['srcLength'])

            # offset + length = end
            slices1_end = np.array(slices1_offset) + np.array(slices1_length)
            slices2_end = np.array(slices2_offset) + np.array(slices2_length)

            slices1 = np.array([slices1_offset, slices1_end])
            slices2 = np.array([slices2_offset, slices2_end])
            return slices1, slices2

        
        
    def compare_files(self,feature,feature_name):
        slices1, slices2 = self.get_copied_slices(feature,feature_name)
        # slices2 = self.get_copied_slices(idx2, file2_data.k)
        if len(slices1[0]) == 0:
            return 0, (0, 0), (np.array([]), np.array([]))

        token_overlap1 = np.sum(slices1[1] - slices1[0])
        token_overlap2 = np.sum(slices2[1] - slices2[0])

        if feature['sus_length'] > 0:
            similarity1 = token_overlap1 / feature['sus_length']
        else:
            similarity1 = 0
        if feature['src_length'] > 0:
            similarity2 = token_overlap2 / feature['src_length']
        else:
            similarity2 = 0

        return token_overlap1, (similarity1, similarity2), (slices1, slices2)
    
    
    
    # 第二步被调用的run方法
    """ha:{    src是数据库源码头代码   sus是上传的可疑代码
    "suspicious-document000003.javasource-document1260833.java": {
        "feature1": {...}, "sus_length": ..., "src_length:
    },
    ...
}"""
    def run(self,ha):
        # 2.1 预处理代码
        self._preprocess_code(self.test_files + self.ref_files)
        # 表示可疑文件 和 第 j 个参考文件之间的两个相似度分数
        self.similarity_matrix = np.full(
            (len(self.test_files), len(self.ref_files), 2),
            -1,
            dtype=np.float64,
        )
        # 表示两个文件 token 的重合率
        self.token_overlap_matrix = np.full(
            (len(self.test_files), len(self.ref_files)), -1
        )
        # 存储匹配到的相似片段
        self.slice_matrix = {}
        comparisons = {}

        for i, test_f in enumerate(
            tqdm(self.test_files,
                 bar_format= '   {l_bar}{bar}{r_bar}')
        ):
            testname = os.path.basename(test_f)
            for j, ref_f in enumerate(self.ref_files):
                srcname = os.path.basename(ref_f)
                true_name = testname + srcname
                if true_name not in ha.keys():
                    continue
                if (ref_f, test_f) in comparisons:
                    ref_idx, test_idx = comparisons[(ref_f, test_f)]
                    overlap = self.token_overlap_matrix[ref_idx, test_idx]
                    sim2, sim1 = self.similarity_matrix[ref_idx, test_idx]
                else:
                    overlap, (sim1, sim2), (slices1, slices2) = self.compare_files(feature=ha[true_name],feature_name=true_name)
                    comparisons[(test_f, ref_f)] = (i, j)
                    if slices1.shape[0] != 0:
                        self.slice_matrix[(test_f, ref_f)] = [slices1, slices2]

                self.similarity_matrix[i, j] = np.array([sim1, sim2])
                self.token_overlap_matrix[i, j] = overlap

    def highlight_overlap_2(self,doc, slices, left_hl, right_hl,
                          truncate=-1, escape_html=False):

        if slices.shape[0] > 0:
            hl_percent = np.sum(slices[1] - slices[0]) / len(doc)
        else:
            warnings.warn("empty slices array")
            return doc, 0

        new_doc = ""
        current_idx = 0
        for slice_idx in range(slices.shape[1]):
            start_idx = slices[0, slice_idx]
            end_idx = slices[1, slice_idx]

            if escape_html:
                pre_highlight = str(escape(doc[current_idx:start_idx]))
                highlighted = left_hl + str(escape(doc[start_idx:end_idx])) + right_hl
            else:
                pre_highlight = doc[current_idx:start_idx]
                highlighted = left_hl + doc[start_idx:end_idx] + right_hl

            if truncate >= 0:
                lines = pre_highlight.split("\n")
                if slice_idx != 0 and len(lines) > truncate * 2:
                    pre_highlight = ("\n".join(lines[:truncate + 1]) + "\n\n...\n\n"
                                     + "\n".join(lines[-truncate - 1:]))
                elif len(lines) > truncate:
                    pre_highlight = "\n".join(lines[-truncate - 1:])
            
            new_doc += pre_highlight
            new_doc += highlighted

            
            current_idx = end_idx

        if escape_html:
            post_highlight = str(escape(doc[current_idx:]))
        else:
            post_highlight = doc[current_idx:]

        if truncate >= 0:
            lines = post_highlight.split("\n")
            if len(lines) > truncate:
                post_highlight = "\n".join(lines[:truncate])
        new_doc += post_highlight

        return new_doc, hl_percent

    def highlight_overlap_1(self,doc, slices, left_hl, right_hl,
                          truncate=-1, escape_html=False):
        if slices.shape[0] > 0:
            hl_percent = np.sum(slices[1] - slices[0]) / len(doc)
        else:
            warnings.warn("empty slices array")
            return doc, 0

        new_doc = ""
        current_idx = 0
        for slice_idx in range(slices.shape[1]):
            start_idx = slices[0, slice_idx]
            end_idx = slices[1, slice_idx]
            num_2 = 0
            start_2 = 0

            if escape_html:
                pre_highlight = str(escape(doc[current_idx:start_idx]))
                highlighted = left_hl + str(escape(doc[start_idx:end_idx])) + right_hl
            else:
                pre_highlight = doc[current_idx:start_idx]
                highlighted = left_hl + doc[start_idx:end_idx] + right_hl

            if truncate >= 0:
                lines = pre_highlight.split("\n")
                if slice_idx != 0 and len(lines) > truncate * 2:
                    pre_highlight = ("\n".join(lines[:truncate + 1]) + "\n\n...\n\n"
                                     + "\n".join(lines[-truncate - 1:]))
                elif len(lines) > truncate:
                    pre_highlight = "\n".join(lines[-truncate - 1:])
            
            new_doc += pre_highlight
            new_doc += highlighted

            
            current_idx = end_idx

        if escape_html:
            post_highlight = str(escape(doc[current_idx:]))
        else:
            post_highlight = doc[current_idx:]

        if truncate >= 0:
            lines = post_highlight.split("\n")
            if len(lines) > truncate:
                post_highlight = "\n".join(lines[:truncate])
        new_doc += post_highlight

        return new_doc, hl_percent

    def get_copied_code_list(self):
        if len(self.similarity_matrix) == 0:
            logging.error("Cannot generate code list: no files compared")
            return []
        x, y = np.where(self.similarity_matrix[:, :, 0] > self.conf.display_t)

        code_list = []
        file_pairs = set()
        for idx in range(len(x)):
            test_f = self.test_files[x[idx]]
            ref_f = self.ref_files[y[idx]]
            if (ref_f, test_f) in file_pairs:
                # if comparison is already in report, don't add it again
                continue
            file_pairs.add((test_f, ref_f))

            test_sim = self.similarity_matrix[x[idx], y[idx], 0]
            ref_sim = self.similarity_matrix[x[idx], y[idx], 1]
            if (test_f, ref_f) in self.slice_matrix:
                slices_test = self.slice_matrix[(test_f, ref_f)][0]
                slices_ref = self.slice_matrix[(test_f, ref_f)][1]
            else:
                 continue


            if self.conf.truncate:
                truncate = 10
            else:
                truncate = -1
            # 3.2调用higtlight_overlap_1方法获得高亮代码 
            hl_code_1, _ = self.highlight_overlap_1(
                self.file_data[test_f].raw_code, slices_test,
                "<span class='highlight-red'>", "</span>",
                truncate=truncate, escape_html=True)
            hl_code_2, _ = self.highlight_overlap_2(
                self.file_data[ref_f].raw_code, slices_ref,
                "<span class='highlight-green'>", "</span>",
                truncate=truncate, escape_html=True)
            overlap = self.token_overlap_matrix[x[idx], y[idx]]
            # 前端被渲染的code值
            code_list.append([test_sim, ref_sim, test_f, ref_f,
                              hl_code_1, hl_code_2, overlap])

        code_list.sort(key=lambda x: -x[0])
    
        return code_list
    
    
    def generate_html_report(self, output_mode="save"):
            if len(self.similarity_matrix) == 0:
                logging.error("Cannot generate report: no files compared")
                return
            # 3.1调用get_copied_code_list方法获得代码列表
            code_list = self.get_copied_code_list()
            data_dir = current_dir+"/templates/"
            # 原来路径： r"/usr/t-3058/detect/sys/copydetect/templates/"
            plot_mtx = np.copy(self.similarity_matrix[:, :, 0])
            plot_mtx[plot_mtx == -1] = np.nan
            plt.imshow(plot_mtx)
            plt.colorbar()
            plt.tight_layout()
            sim_mtx_buffer = io.BytesIO()
            plt.savefig(sim_mtx_buffer)
            sim_mtx_buffer.seek(0)
            sim_mtx_base64 = base64.b64encode(sim_mtx_buffer.read()).decode()
            plt.close()

            scores = self.similarity_matrix[:, :, 0][self.similarity_matrix[:, :, 0] != -1]
            plt.hist(scores, bins=20)
            plt.tight_layout()
            sim_hist_buffer = io.BytesIO()
            plt.savefig(sim_hist_buffer)
            sim_hist_buffer.seek(0)
            sim_hist_base64 = base64.b64encode(sim_hist_buffer.read()).decode()
            plt.close()

            # render template with jinja and save as html
            with open(data_dir + "report.html", encoding="utf-8") as template_fp:
                template = Template(template_fp.read())

            flagged = self.similarity_matrix[:, :, 0] > self.conf.display_t
            flagged_file_count = np.sum(np.any(flagged, axis=1))

            formatted_conf = json.dumps(self.conf.to_json(), indent=4)
            output = template.render(config_params=formatted_conf,
                                     version=1.0,
                                     test_count=len(self.test_files),
                                     test_files=self.test_files,
                                     compare_count=len(self.ref_files),
                                     compare_files=self.ref_files,
                                     flagged_file_count=flagged_file_count,
                                     code_list=code_list,
                                     sim_mtx_base64=sim_mtx_base64,
                                     sim_hist_base64=sim_hist_base64)

            if output_mode == "save":
                with open(self.conf.out_file, "w", encoding="utf-8") as report_f:
                    report_f.write(output)

                if not self.conf.silent:
                    print(
                        f"Output saved to {self.conf.out_file.replace('//', '/')}"
                    )
                if self.conf.autoopen:
                    webbrowser.open(
                        'file://' + str(Path(self.conf.out_file).resolve())
                    )
            elif output_mode == "return":
                return output
            else:
                raise ValueError("output_mode not supported")
