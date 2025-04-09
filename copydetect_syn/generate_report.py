from docx import Document
from docx.shared import Pt, Inches, Cm, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.oxml import parse_xml
from docx.oxml.ns import nsdecls
from docx.oxml.ns import qn
import os


def create_government_blockchain_report(
    stored_code_values,data,
    output_file="政务区块链基础平台项目源代码自研率检测报告.docx",
):
    # print(stored_code_values)
    # print(data)
    # 创建新文档
    doc = Document()

    # 设置正文中文字体为宋体，英文字体为Times New Roman
    def set_run_font(run, size=Pt(12), bold=False, color=None):
        font = run.font
        font.size = size
        font.bold = bold
        if color:
            font.color.rgb = color

        # 设置英文字体
        run.font.name = "Times New Roman"

        # 设置中文字体为宋体
        run._element.rPr.rFonts.set(qn('w:eastAsia'), '宋体')

    # 设置页面边距
    sections = doc.sections
    for section in sections:
        section.top_margin = Cm(2.54)
        section.bottom_margin = Cm(2.54)
        section.left_margin = Cm(3.17)
        section.right_margin = Cm(3.17)

    # === 第一页：封面 ===
    # 添加页眉标题
    header_para = doc.sections[0].header.paragraphs[0]
    header_run = header_para.add_run("《政务区块链基础平台》项目源代码自研率检测报告")
    header_para.alignment = WD_ALIGN_PARAGRAPH.CENTER
    set_run_font(header_run, size=Pt(12))

    # 添加主标题（居中）
    for _ in range(8):  # 添加空行
        doc.add_paragraph().alignment = WD_ALIGN_PARAGRAPH.CENTER

    main_title = doc.add_paragraph()
    main_title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    title_run = main_title.add_run("《政务区块链基础平台》项目\n源代码自研率检测报告")
    set_run_font(title_run, size=Pt(22), bold=True)

    # 添加底部信息
    for _ in range(12):  # 添加空行
        doc.add_paragraph().alignment = WD_ALIGN_PARAGRAPH.CENTER

    bottom_info = doc.add_paragraph()
    bottom_info.alignment = WD_ALIGN_PARAGRAPH.CENTER
    bottom_run = bottom_info.add_run("编制单位：佛山大学\n2024 年 10 月")
    set_run_font(bottom_run, size=Pt(14))

    # 添加分页符
    doc.add_page_break()

    # === 第二页：内容 ===
    # 设置标题
    title = doc.add_paragraph()
    title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    title_run = title.add_run("《政务区块链基础平台》项目源代码自研率检测报告")
    set_run_font(title_run, size=Pt(16), bold=True)

    # 添加检测结果标题
    h1 = doc.add_paragraph()
    h1_run = h1.add_run("1. 检测结果概述")
    set_run_font(h1_run, bold=True)

    # 添加检测参数说明
    if data:
        params_title = doc.add_paragraph()
        params_run = params_title.add_run("1.1 检测参数配置")
        set_run_font(params_run, bold=True)
        
        params_text = doc.add_paragraph()
        # 处理Java ArrayList对象
        try:
            # 尝试解析Java ArrayList数据
            data_length = 0
            suspicious_files = set()
            reference_files = set()
            match_count = 0
            empty_match_count = 0
            
            # 转换为字符串并解析
            data_str = str(data)
            if '[' in data_str and ']' in data_str:
                # 提取比较文件信息
                import json
                try:
                    # 尝试将字符串转换为Python列表/字典
                    data_items = json.loads(data_str)
                    data_length = len(data_items)
                    
                    for item in data_items:
                        for key in item:
                            parts = key.split("java")
                            if len(parts) >= 2:
                                suspicious = parts[0] + "java"
                                reference = "java" + parts[1]
                                suspicious_files.add(suspicious)
                                reference_files.add(reference)
                                
                                if "feature1" in item[key] and item[key]["feature1"]:
                                    match_count += 1
                                else:
                                    empty_match_count += 1
                except:
                    pass
            
            params_info = f"""检测参数配置如下：
- 检测比对项数: {data_length}
- 被检测文件数: {len(suspicious_files)}
- 参考文件数: {len(reference_files)}
- 有匹配特征的比对项: {match_count}
- 无匹配特征的比对项: {empty_match_count}

比对详情摘要:
"""
            # 添加前5个匹配项的简要信息
            try:
                for i, item in enumerate(json.loads(data_str)):
                    if i >= 5:  # 只显示前5个
                        break
                    for key in item:
                        # 直接从item字典中获取sus_length和src_length
                        sus_length = item[key].get('sus_length', '未知')
                        src_length = item[key].get('src_length', '未知')
                        
                        # 检查是否有feature1
                        if "feature1" in item[key] and item[key]["feature1"]:
                            feature_length = item[key]["feature1"].get('length', 0)
                            features = f"匹配({feature_length}字节)"
                        else:
                            features = "无匹配特征"
                            
                        params_info += f"- 项目{i+1}: 可疑文件({sus_length}字节) vs 参考文件({src_length}字节) - {features}\n"
                
                if data_length > 5:
                    params_info += f"... 以及其他 {data_length - 5} 个比对项"
            except:
                params_info += "- 详细信息解析失败\n"
                
            # 添加一个检测数据表格
            try:
                # 创建检测数据表格
                params_info += "\n\n检测数据详情表格："
                params_run = params_text.add_run(params_info)
                set_run_font(params_run)
                
                # 创建表格
                match_table = doc.add_table(rows=1, cols=5)
                match_table.style = "Table Grid"
                
                # 设置表头
                header_cells = match_table.rows[0].cells
                headers = ["待检测代码文件名", "对应源代码文件", "重复代码详情", "待检测代码长度", "对应源代码长度"]
                for i, text in enumerate(headers):
                    run = header_cells[i].paragraphs[0].add_run(text)
                    set_run_font(run, bold=True)
                
                # 填充数据
                data_items = json.loads(data_str)
                for item in data_items:
                    for key in item:
                        row = match_table.add_row()
                        cells = row.cells
                        
                        # 拆分文件名
                        parts = key.split("java")
                        if len(parts) >= 2:
                            suspicious_file = parts[0] + "java"
                            reference_file = "java" + parts[1]
                            
                            # 去除前缀，只保留文件名
                            suspicious_name = suspicious_file.replace("suspicious-document", "")
                            reference_name = reference_file.replace("source-document", "")
                            
                            # 第1列：待检测代码文件名
                            cells[0].text = suspicious_name
                            
                            # 第2列：对应源代码文件
                            cells[1].text = reference_name
                            
                            # 第3列：重复代码详情
                            if "feature1" in item[key] and item[key]["feature1"]:
                                offset = item[key]["feature1"].get("offset", 0)
                                length = item[key]["feature1"].get("length", 0)
                                src_offset = item[key]["feature1"].get("srcOffset", 0)
                                src_length = item[key]["feature1"].get("srcLength", 0)
                                cells[2].text = f"[{offset}:({offset+length})]-{src_offset}:{src_length}"
                            else:
                                cells[2].text = "无匹配特征"
                            
                            # 第4列：待检测代码长度
                            sus_length = item[key].get("sus_length", "未知")
                            cells[3].text = str(sus_length)
                            
                            # 第5列：对应源代码长度
                            src_length = item[key].get("src_length", "未知")
                            cells[4].text = str(src_length)
                            
                            # 设置单元格字体
                            for cell in cells:
                                for paragraph in cell.paragraphs:
                                    for run in paragraph.runs:
                                        set_run_font(run)
                
                # 限制表格最大显示10行
                if len(match_table.rows) > 11:  # 算上表头行
                    while len(match_table.rows) > 11:
                        match_table._tbl.remove(match_table.rows[-1]._tr)
                    
                    # 添加说明行
                    row = match_table.add_row()
                    cells = row.cells
                    cells[0].merge(cells[4])
                    cells[0].text = f"... 等共 {len(data_items)} 项匹配数据 (仅显示前10项)"
                    for paragraph in cells[0].paragraphs:
                        for run in paragraph.runs:
                            set_run_font(run, bold=True)
                            
            except Exception as e:
                error_para = doc.add_paragraph()
                error_run = error_para.add_run(f"解析检测数据失败: {str(e)}")
                set_run_font(error_run, color=RGBColor(255, 0, 0))
                
        except Exception as e:
            params_info = f"""检测参数配置如下：
- 无法解析的检测参数: {type(data).__name__}
- 错误: {str(e)}
- 原始数据: {str(data)[:200]}...
            """
        params_run = params_text.add_run(params_info)
        set_run_font(params_run)
    
    # 添加检测结果摘要
    results_title = doc.add_paragraph()
    results_run = results_title.add_run("1.2 检测结果摘要")
    set_run_font(results_run, bold=True)
    
    if stored_code_values:
        # 计算平均值
        avg_similarity = sum(item['code0'] for item in stored_code_values) / len(stored_code_values) if stored_code_values else 0
        avg_coverage = sum(item['code7'] for item in stored_code_values) / len(stored_code_values) if stored_code_values else 0
        total_lines = sum(item['code8'] for item in stored_code_values)
        covered_lines = sum(item['code9'] for item in stored_code_values)
        overall_coverage = covered_lines / total_lines if total_lines > 0 else 0
        
        summary_text = doc.add_paragraph()
        summary_info = f"""检测结果摘要：
- 检测文件数量: {len(stored_code_values)}
- 平均相似度: {avg_similarity:.2%}
- 平均行覆盖率: {avg_coverage:.2%}
- 总行数: {total_lines}
- 覆盖行数: {covered_lines}
- 整体行覆盖率: {overall_coverage:.2%}
- 自研率评估: {(1-overall_coverage):.2%}
        """
        summary_run = summary_text.add_run(summary_info)
        set_run_font(summary_run)
    
    # 添加详细检测结果表格
    if stored_code_values:
        details_title = doc.add_paragraph()
        details_run = details_title.add_run("2. 详细检测结果")
        set_run_font(details_run, bold=True)
        
        table_title = doc.add_paragraph()
        table_run = table_title.add_run("表 2 代码相似度与覆盖率详情")
        set_run_font(table_run)
        
        # 创建详细结果表格
        table = doc.add_table(rows=1, cols=7)
        table.style = "Table Grid"
        
        # 设置表头
        header_cells = table.rows[0].cells
        headers = ["序号", "测试文件", "参考文件", "相似度", "重叠Token数", "代码行数", "覆盖率"]
        for i, text in enumerate(headers):
            run = header_cells[i].paragraphs[0].add_run(text)
            set_run_font(run, bold=True)
        
        # 填充数据
        for i, item in enumerate(stored_code_values):
            row = table.add_row()
            cells = row.cells
            
            # 添加数据到表格
            cells[0].text = str(i+1)
            
            # 提取文件名而不是完整路径
            test_file = os.path.basename(item['code2'])
            ref_file = os.path.basename(item['code3'])
            
            cells[1].text = test_file
            cells[2].text = ref_file
            cells[3].text = f"{item['code0']:.2%}"
            cells[4].text = str(item['code6'])
            cells[5].text = f"{item['code8']} 行"
            cells[6].text = f"{item['code7']:.2%}"
            
            # 根据覆盖率设置颜色
            if item['code7'] > 0.7:
                for paragraph in cells[6].paragraphs:
                    for run in paragraph.runs:
                        set_run_font(run, color=RGBColor(255, 0, 0))  # 红色
            
            # 设置单元格字体
            for cell in cells:
                for paragraph in cell.paragraphs:
                    for run in paragraph.runs:
                        set_run_font(run)
    
    # 添加高风险文件分析
    if stored_code_values:
        high_risk = [item for item in stored_code_values if item['code7'] > 0.5]
        if high_risk:
            risk_title = doc.add_paragraph()
            risk_run = risk_title.add_run("3. 高风险文件分析")
            set_run_font(risk_run, bold=True)
            
            for i, item in enumerate(high_risk):
                file_title = doc.add_paragraph()
                file_run = file_title.add_run(f"3.{i+1} {os.path.basename(item['code2'])}")
                set_run_font(file_run, bold=True)
                
                analysis = doc.add_paragraph()
                analysis_text = f"""文件路径: {item['code2']}
参考文件: {item['code3']}
相似度: {item['code0']:.2%}
覆盖率: {item['code7']:.2%}
总行数: {item['code8']}
覆盖行数: {item['code9']}
风险评估: {'高风险' if item['code7'] > 0.7 else '中等风险'}

分析结论: 该文件与参考源码存在较高相似度，建议进行代码重构或标明引用来源。
                """
                analysis_run = analysis.add_run(analysis_text)
                set_run_font(analysis_run)
    
    # 添加结论
    conclusion_title = doc.add_paragraph()
    conclusion_run = conclusion_title.add_run("4. 检测结论")
    set_run_font(conclusion_run, bold=True)
    
    conclusion = doc.add_paragraph()
    if stored_code_values:
        overall_score = 1 - overall_coverage
        if overall_score > 0.8:
            assessment = "优秀，代码自研率高"
        elif overall_score > 0.6:
            assessment = "良好，代码自研率中等"
        else:
            assessment = "较差，代码自研率低"
        
        conclusion_text = f"""根据检测结果，项目代码自研率为 {overall_score:.2%}，评价为{assessment}。
检测共发现 {len(stored_code_values)} 个相似文件，其中高风险文件 {len(high_risk)} 个。
建议对高风险文件进行重构或明确标注引用来源，以提高代码质量和自研水平。
        """
    else:
        conclusion_text = "未检测到有效的相似度数据，无法给出准确评估。"
    
    conclusion_run = conclusion.add_run(conclusion_text)
    set_run_font(conclusion_run)

    # 保存文档
    doc.save(output_file)
    return output_file


if __name__ == "__main__":
    output_file = create_government_blockchain_report()
    print(f"报告已生成: {output_file}")
    print(f"报告已生成")
