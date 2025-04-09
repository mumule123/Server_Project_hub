import logging
from jinja2 import Template, Environment, FileSystemLoader
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
import numpy as np
from flask import session
from concurrent.futures import ThreadPoolExecutor
import functools
import threading

current_dir = os.path.dirname(os.path.abspath(__file__))

# 在文件开头添加全局变量
stored_code_values = None

# 创建一个全局变量存储编译后的模板
_template_cache = {}

# 添加文件缓存
_file_cache = {}
_file_cache_lock = threading.Lock()

def get_template(template_path):
    """获取预编译的模板"""
    global _template_cache
    
    if template_path not in _template_cache:
        # 获取模板所在目录
        template_dir = os.path.dirname(template_path)
        template_name = os.path.basename(template_path)
        
        # 创建环境并加载模板
        env = Environment(loader=FileSystemLoader(template_dir))
        _template_cache[template_path] = env.get_template(template_name)
    
    return _template_cache[template_path]

def get_file_content(file_path, encoding="utf-8"):
    """获取文件内容，使用缓存"""
    global _file_cache, _file_cache_lock
    
    with _file_cache_lock:
        if file_path in _file_cache:
            return _file_cache[file_path]
            
        try:
            with open(file_path, encoding=encoding) as f:
                content = f.read()
                _file_cache[file_path] = content
                return content
        except Exception as e:
            logging.error(f"Error reading file {file_path}: {e}")
            return ""

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
            code = get_file_content(file, encoding)

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
        self._executor = ThreadPoolExecutor(max_workers=4)  # 添加线程池

    def _preprocess_code(self, file_list):
        """并行预处理代码文件"""
        def process_file(code_f):
            if code_f not in self.file_data:
                try:
                    self.file_data[code_f] = CodeFingerprint(
                        code_f, self.conf.noise_t, self.conf.window_size,
                        None, not self.conf.disable_filtering,
                        self.conf.force_language, encoding=self.conf.encoding)
                except UnicodeDecodeError:
                    logging.warning(f"Skipping {code_f}: file not UTF-8 text")
                    return None
            return code_f

        # 使用线程池并行处理文件
        futures = []
        for code_f in file_list:
            futures.append(self._executor.submit(process_file, code_f))
        
        # 等待所有任务完成
        for future in futures:
            future.result()

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
        """优化后的运行方法"""
        # 并行预处理代码
        self._preprocess_code(self.test_files + self.ref_files)
        
        # 初始化矩阵
        self.similarity_matrix = np.full(
            (len(self.test_files), len(self.ref_files), 2),
            -1,
            dtype=np.float64,
        )
        self.token_overlap_matrix = np.full(
            (len(self.test_files), len(self.ref_files)), -1
        )
        self.slice_matrix = {}
        
        # 使用线程池并行处理比较
        def process_comparison(args):
            i, test_f, j, ref_f = args
            testname = os.path.basename(test_f)
            srcname = os.path.basename(ref_f)
            true_name = testname + srcname
            
            if true_name not in ha.keys():
                return None
                
            overlap, (sim1, sim2), (slices1, slices2) = self.compare_files(
                feature=ha[true_name], feature_name=true_name)
            
            if slices1.shape[0] != 0:
                self.slice_matrix[(test_f, ref_f)] = [slices1, slices2]
            
            return (i, j, overlap, sim1, sim2)
        
        # 准备比较任务
        comparison_tasks = []
        for i, test_f in enumerate(self.test_files):
            for j, ref_f in enumerate(self.ref_files):
                comparison_tasks.append((i, test_f, j, ref_f))
        
        # 并行执行比较
        futures = []
        for task in comparison_tasks:
            futures.append(self._executor.submit(process_comparison, task))
        
        # 收集结果
        for future in futures:
            result = future.result()
            if result is not None:
                i, j, overlap, sim1, sim2 = result
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

    def highlight_overlap_1(self, doc, slices, left_hl, right_hl, truncate=-1, escape_html=False):
        if slices.shape[0] == 0:
            warnings.warn("empty slices array")
            return doc, 0

        # 计算高亮百分比
        hl_percent = np.sum(slices[1] - slices[0]) / len(doc)
        
        # 使用列表存储结果，避免频繁字符串拼接
        result_parts = []
        current_idx = 0
        
        for slice_idx in range(slices.shape[1]):
            start_idx = slices[0, slice_idx]
            end_idx = slices[1, slice_idx]
            
            # 处理高亮前的文本
            pre_highlight = doc[current_idx:start_idx]
            if escape_html:
                pre_highlight = str(escape(pre_highlight))
            
            # 处理高亮文本
            highlighted = doc[start_idx:end_idx]
            if escape_html:
                highlighted = str(escape(highlighted))
            highlighted = left_hl + highlighted + right_hl
            
            # 处理截断
            if truncate >= 0:
                lines = pre_highlight.split("\n")
                if slice_idx != 0 and len(lines) > truncate * 2:
                    pre_highlight = ("\n".join(lines[:truncate + 1]) + "\n\n...\n\n"
                                   + "\n".join(lines[-truncate - 1:]))
                elif len(lines) > truncate:
                    pre_highlight = "\n".join(lines[-truncate - 1:])
            
            result_parts.append(pre_highlight)
            result_parts.append(highlighted)
            current_idx = end_idx
        
        # 处理剩余文本
        post_highlight = doc[current_idx:]
        if escape_html:
            post_highlight = str(escape(post_highlight))
        
        if truncate >= 0:
            lines = post_highlight.split("\n")
            if len(lines) > truncate:
                post_highlight = "\n".join(lines[:truncate])
        
        result_parts.append(post_highlight)
        
        # 一次性拼接所有部分
        return "".join(result_parts), hl_percent

    def highlight_overlap_char_level(self, doc1, doc2, left_hl, right_hl, escape_html=False):
        """使用字符级别比较来高亮显示相似代码
        
        通过直接比较原始字符串，找出最长公共子串进行高亮
        """
        if not doc1 or not doc2:
            return doc1, 0

        # 预处理文档，去除空白字符以便更好地匹配
        def preprocess_doc(doc):
            # 保留换行符以保持代码结构，但移除多余空格
            lines = doc.splitlines()
            processed_lines = [line.strip() for line in lines]
            return '\n'.join(processed_lines)

        processed_doc1 = preprocess_doc(doc1)
        processed_doc2 = preprocess_doc(doc2)

        # 使用最长公共子串算法找出相似片段
        def find_longest_common_substrings(s1, s2, min_length=10):
            """找出两个字符串中所有长度大于min_length的公共子串"""
            result = []
            # 构建二维数组来存储公共子串长度
            m, n = len(s1), len(s2)
            dp = [[0 for _ in range(n+1)] for _ in range(m+1)]

            # 找出所有公共子串
            for i in range(1, m+1):
                for j in range(1, n+1):
                    if s1[i-1] == s2[j-1]:
                        dp[i][j] = dp[i-1][j-1] + 1
                        if dp[i][j] >= min_length:
                            # 回溯找出完整的子串
                            length = dp[i][j]
                            substring = s1[i-length:i]
                            # 检查是否已经存在包含此子串的更长子串
                            is_contained = False
                            for existing in result[:]:
                                if substring in existing[0]:
                                    is_contained = True
                                    break
                                elif existing[0] in substring:
                                    result.remove(existing)
                            if not is_contained:
                                result.append((substring, i-length, i, j-length, j))

            # 按长度排序以优先处理较长的公共子串
            result.sort(key=lambda x: -len(x[0]))
            return result

        # 找出公共子串
        common_substrings = find_longest_common_substrings(processed_doc1, processed_doc2)

        # 标记需要高亮的区域
        highlight_regions = []
        for substring, start1, end1, _, _ in common_substrings:
            # 在原始文档中找到对应位置
            orig_start = processed_doc1.find(substring, 0 if not highlight_regions else highlight_regions[-1][1])
            if orig_start != -1:
                highlight_regions.append((orig_start, orig_start + len(substring)))

        # 应用高亮
        highlight_regions.sort()
        new_doc = ""
        current_idx = 0

        for start, end in highlight_regions:
            if start < current_idx:  # 跳过重叠区域
                continue

            if escape_html:
                pre_highlight = str(escape(doc1[current_idx:start]))
                highlighted = left_hl + str(escape(doc1[start:end])) + right_hl
            else:
                pre_highlight = doc1[current_idx:start]
                highlighted = left_hl + doc1[start:end] + right_hl

            new_doc += pre_highlight + highlighted
            current_idx = end

        # 添加最后一部分
        if escape_html:
            post_highlight = str(escape(doc1[current_idx:]))
        else:
            post_highlight = doc1[current_idx:]
        new_doc += post_highlight

        # 计算高亮百分比
        hl_chars = sum(end - start for start, end in highlight_regions)
        hl_percent = hl_chars / len(doc1) if len(doc1) > 0 else 0

        return new_doc, hl_percent

    def calculate_line_coverage(self, doc, slices):
        """计算代码行级的覆盖比例
        
        Parameters
        ----------
        doc : str
            原始代码文本
        slices : array
            抄袭代码的切片位置
            
        Returns
        -------
        tuple
            (coverage_ratio, total_lines, covered_line_count)
            - coverage_ratio: 行覆盖率 (抄袭行数/总行数)
            - total_lines: 总行数
            - covered_line_count: 抄袭行数
        """
        if slices.shape[0] == 0:
            return 0, 0, 0
            
        # 获取总行数
        total_lines = doc.count('\n') + 1
        
        # 标记被抄袭的行
        covered_lines = set()
        for slice_idx in range(slices.shape[1]):
            start_idx = slices[0, slice_idx]
            end_idx = slices[1, slice_idx]
            
            # 计算起始位置在哪一行
            start_line = doc[:start_idx].count('\n') + 1
            # 计算结束位置在哪一行
            end_line = doc[:end_idx].count('\n') + 1
            
            # 将所有被覆盖的行添加到集合中
            for line_num in range(start_line, end_line + 1):
                covered_lines.add(line_num)
        
        # 计算覆盖率和抄袭行数
        covered_line_count = len(covered_lines)
        coverage_ratio = covered_line_count / total_lines if total_lines > 0 else 0
        return coverage_ratio, total_lines, covered_line_count

    def get_copied_code_list(self, highlight_method="default"):
        """获取抄袭代码列表，并按指定方法进行高亮"""
        if len(self.similarity_matrix) == 0:
            logging.error("Cannot generate code list: no files compared")
            return []
            
        # 使用 numpy 的 where 函数优化查找
        x, y = np.where(self.similarity_matrix[:, :, 0] > self.conf.display_t)
        
        # 预编译高亮标签
        highlight_red_start = "<span class='highlight-red'>"
        highlight_red_end = "</span>"
        highlight_green_start = "<span class='highlight-green'>"
        highlight_green_end = "</span>"
        
        code_list = []
        file_pairs = set()
        
        # 批量处理文件对
        for idx in range(len(x)):
            test_f = self.test_files[x[idx]]
            ref_f = self.ref_files[y[idx]]
            
            # 跳过已处理的文件对
            if (ref_f, test_f) in file_pairs:
                continue
            file_pairs.add((test_f, ref_f))
            
            # 获取相似度分数
            test_sim = self.similarity_matrix[x[idx], y[idx], 0]
            ref_sim = self.similarity_matrix[x[idx], y[idx], 1]
            
            # 获取切片
            slices_test = self.slice_matrix.get((test_f, ref_f), [None, None])[0]
            slices_ref = self.slice_matrix.get((test_f, ref_f), [None, None])[1]
            
            if slices_test is None or slices_ref is None:
                slices_test = self.slice_matrix.get((ref_f, test_f), [None, None])[1]
                slices_ref = self.slice_matrix.get((ref_f, test_f), [None, None])[0]
            
            if slices_test is None or slices_ref is None:
                continue
                
            # 设置截断参数
            truncate = 10 if self.conf.truncate else -1
            
            # 获取原始代码
            test_code = self.file_data[test_f].raw_code
            ref_code = self.file_data[ref_f].raw_code
            
            # 并行处理高亮
            with ThreadPoolExecutor(max_workers=4) as executor:
                # 提交高亮任务
                futures = []
                futures.append(executor.submit(
                    self.highlight_overlap_1,
                    test_code, slices_test,
                    highlight_red_start, highlight_red_end,
                    truncate, True
                ))
                futures.append(executor.submit(
                    self.highlight_overlap_2,
                    ref_code, slices_ref,
                    highlight_green_start, highlight_green_end,
                    truncate, True
                ))
                futures.append(executor.submit(
                    self.highlight_overlap_char_level,
                    test_code, ref_code,
                    highlight_red_start, highlight_red_end,
                    True
                ))
                futures.append(executor.submit(
                    self.highlight_overlap_char_level,
                    ref_code, test_code,
                    highlight_green_start, highlight_green_end,
                    True
                ))
                
                # 获取结果
                hl_code_1, _ = futures[0].result()
                hl_code_2, _ = futures[1].result()
                char_level_1, _ = futures[2].result()
                char_level_2, _ = futures[3].result()
            
            # 计算重叠和覆盖率
            overlap = self.token_overlap_matrix[x[idx], y[idx]]
            coverage_ratio, total_lines, covered_lines = self.calculate_line_coverage(
                test_code, slices_test)
            
            # 添加到结果列表
            code_list.append([
                test_sim, ref_sim, test_f, ref_f,
                hl_code_1, hl_code_2, overlap,
                coverage_ratio, total_lines, covered_lines,
                char_level_1, char_level_2  # 添加字符级高亮结果
            ])
        
        # 按相似度排序
        code_list.sort(key=lambda x: -x[0])
        return code_list

    def generate_html_report(self, output_mode="save"):
        """优化后的报告生成方法"""
        global stored_code_values
        
        if len(self.similarity_matrix) == 0:
            logging.error("Cannot generate report: no files compared")
            return
            
        # 获取代码列表
        code_list = self.get_copied_code_list()
        
        # 准备存储的代码值
        stored_code_values = []
        for item in code_list:
            stored_code_values.append({
                'code0': float(item[0]),
                'code1': float(item[1]),
                'code2': str(item[2]),
                'code3': str(item[3]),
                'code6': int(item[6]),
                'code7': float(item[7]),
                'code8': int(item[8]),
                'code9': int(item[9]),
            })
        
        # 使用预编译的模板
        template = get_template(os.path.join(current_dir, "templates", "report.html"))
        
        # 准备模板数据
        template_data = {
            'config_params': json.dumps(self.conf.to_json(), indent=4),
            'version': 1.0,
            'test_count': len(self.test_files),
            'test_files': self.test_files,
            'compare_count': len(self.ref_files),
            'compare_files': self.ref_files,
            'flagged_file_count': np.sum(np.any(self.similarity_matrix[:, :, 0] > self.conf.display_t, axis=1)),
            'code_list': code_list,
            'sim_mtx_base64': "",
            'sim_hist_base64': ""
        }
        
        # 渲染模板
        output = template.render(**template_data)
        
        if output_mode == "save":
            # 异步保存文件
            def save_report():
                with open(self.conf.out_file, "w", encoding="utf-8") as report_f:
                    report_f.write(output)
                if not self.conf.silent:
                    print(f"Output saved to {self.conf.out_file.replace('//', '/')}")
            
            # 在新线程中保存报告
            threading.Thread(target=save_report).start()
            
        elif output_mode == "return":
            return output
        else:
            raise ValueError("output_mode not supported")

    def __del__(self):
        """清理线程池资源"""
        if hasattr(self, '_executor'):
            self._executor.shutdown(wait=True)
