import time
from flask import (
    Flask,
    request,
    render_template_string,
    send_from_directory,
    render_template,
    redirect,
    jsonify,
    session
)
import re
from elasticsearch import Elasticsearch
import code_main
from flask_cors import CORS
import jpype
import os
import generate_pdf,generate_report

import util
import threading
import traceback
import secrets
from myreport import Report

from config import WORD_REPORT_NAME,PDF_REPORT_NAME

app = Flask(__name__)
app.secret_key = secrets.token_hex(16)
CORS(app)


calc = None  # 传入所需参数
data = None  # 用于存储upcode_file方法中的data，供down_word方法使用


@app.route("/")
def home():
    return redirect("/static/fileyploda80.html")


import socket

import socket
from elasticsearch import Elasticsearch


def get_local_ip():
    """通过连接公网地址的方式，获取本地真实出口 IP"""
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        # 连接一个公网 IP，用来判断本地出口 IP（不实际发出）
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
    finally:
        s.close()
    return ip


def get_es_client():
    local_ip = get_local_ip()
    print(f"当前主机 IP 地址：{local_ip}")

    # 你可以根据这个 IP 来判断是否是服务器
    if local_ip.startswith("10.10.62."):
        print("🔧 运行在服务器环境，连接远程 Elasticsearch")
        return Elasticsearch(
            [{"host": "10.10.62.32", "port": 9200, "scheme": "http"}],
            headers={"Content-Type": "application/json"},
        )
    else:
        print("💻 运行在本地环境，连接本地 Elasticsearch")
        return Elasticsearch([{"host": "127.0.0.1", "port": 9200, "scheme": "http"}])


@app.route("/report")
def serve_report():
    return send_from_directory("./", "report.html")


@app.route("/down_word", methods=["GET"])
def down_word():
    global data

    # 从myreport.py中获取保存的代码值
    from myreport import stored_code_values

    # 获取当前应用所在目录
    current_dir = os.path.dirname(os.path.abspath(__file__))
    # 确保document目录存在
    document_dir = os.path.join(current_dir, 'document')
    if not os.path.exists(document_dir):
        os.makedirs(document_dir)
        
    output_filename = WORD_REPORT_NAME  # 配置文件中的报告名称
    output_path = os.path.join(document_dir, output_filename)

    if stored_code_values:
        # 生成报告并获取文件路径
        try:
            output_file = generate_report.create_government_blockchain_report(
                stored_code_values, data, output_path
            )
        except Exception as e:
            print(f"生成报告时出错: {e}")
            traceback.print_exc()
    else:
        print("警告：stored_code_values为空，无法生成报告")

    # 检查文件是否存在
    if os.path.exists(output_path):
        print(f"准备发送文件: {output_path}")
        # 修正：正确分离目录和文件名
        # 现在directory是document子目录
        return send_from_directory(document_dir, output_filename, as_attachment=True)
    else:
        print(f"文件不存在: {output_path}")
        return jsonify({
            "error": "报告文件不存在",
            "path": output_path
        }), 404

@app.route("/down_pdf", methods=["GET"])
def down_pdf():
    global data

    # 从myreport.py中获取保存的代码值
    from myreport import stored_code_values

    # 获取当前应用所在目录
    current_dir = os.path.dirname(os.path.abspath(__file__))
    # 确保document目录存在
    document_dir = os.path.join(current_dir, 'document')
    if not os.path.exists(document_dir):
        os.makedirs(document_dir)
        
    output_filename = PDF_REPORT_NAME  # 配置文件中的报告名称
    output_path = os.path.join(document_dir, output_filename)

    if stored_code_values:
        # 生成报告并获取文件路径
        try:
            output_file = generate_pdf.create_government_blockchain_report(
                stored_code_values, data, output_path
            )
        except Exception as e:
            print(f"生成报告时出错: {e}")
            traceback.print_exc()
    else:
        print("警告：stored_code_values为空，无法生成报告")

    # 检查文件是否存在
    if os.path.exists(output_path):
        print(f"准备发送文件: {output_path}")
        # 修正：正确分离目录和文件名
        # 现在directory是document子目录
        return send_from_directory(document_dir, output_filename, as_attachment=True)
    else:
        print(f"文件不存在: {output_path}")
        return jsonify({
            "error": "报告文件不存在",
            "path": output_path
        }), 404


# 上传文件
@app.route("/upcode_file", methods=["POST"])
def upload_file():
    global data
    print("进入了")
    htmlres = """<!doctype html>
    <title>文件上传</title>
    <h1>上传文件</h1>
    <form method=post enctype=multipart/form-data>
      <input type=file name=file>
      <input type=submit value=上传>
    </form>"""
    n = 10
    top_n = 10
    index_name = "myindex_java"
    check_file_suf = ".txt"
    file_path = ""
    search_folder = ""

    try:
        if request.method == "POST":
            # 检查是否有文件被上传
            if "file" not in request.files:
                return "没有选择文件"
            file = request.files["file"]
            # 检查文件名是否为空
            if file.filename == "":
                return "文件名为空"
            # 保存上传的文件到服务器的临时文件夹
            # 服务器本来的地址
            # upload_folder = r'/usr/t-3058/detect/sys/copydetect/uploads'
            # search_folder = r'/usr/t-3058/detect/sys/copydetect/downs'
            # 获得相对地址
            upload_folder = current_dir + "/uploads"
            search_folder = current_dir + "/downs"

            # 上传文件夹
            if not os.path.exists(upload_folder):
                os.makedirs(upload_folder)
            # 检索文件夹

            if not os.path.exists(search_folder):
                os.makedirs(search_folder)

            new_filename = file.filename.split(sep=".")
            file_path = os.path.join(upload_folder, new_filename[0] + ".java")
            file.save(file_path)

            # 原来的检索库地址
            # es = Elasticsearch(
            #     [{'host': '10.10.62.32', 'port': 9200, 'scheme': 'http'}],
            #     headers={"Content-Type": "application/json"}
            # )

            es = get_es_client()

            # 读取文件内容

            words = util.get_string_gram(file_path)
            query_phrases = []
            search_arr = []

            # 开始写检索逻辑

            for i in range(len(words) - n + 1):
                ngram = " ".join(words[i : i + n])
                query_phrases.append(ngram)
                if len(query_phrases) >= 1000:
                    tem_ans = []
                    should_clauses = [
                        {"match_phrase": {"desc": phrase}} for phrase in query_phrases
                    ]
                    query_body = {
                        "_source": {},
                        "query": {"bool": {"should": should_clauses}},
                        "from": 0,
                        "size": 10,
                    }
                    response = es.search(
                        index=index_name, body=query_body, request_timeout=30
                    )
                    #
                    # # 处理查询结果
                    cc = 0
                    for se_res in response["hits"]["hits"]:
                        if cc >= top_n:
                            break
                        search_arr.append(
                            {
                                "k": str(se_res["_source"]["id"]),
                                "v": str(se_res["_score"]),
                                "n": str(se_res["_source"]["desc"]),
                            }
                        )
                    cc += 1
                    query_phrases = []
            should_clauses = [
                {"match_phrase": {"desc": phrase}} for phrase in query_phrases
            ]
            query_body = {
                "_source": {},
                "query": {"bool": {"should": should_clauses}},
                "from": 0,
                "size": 10,
            }

            if len(query_phrases) > 0:
                response = es.search(
                    index=index_name, body=query_body, request_timeout=30
                )
                cc = 0
                tem_ans = []
                for se_res in response["hits"]["hits"]:
                    if cc >= top_n:
                        break
                    search_arr.append(
                        {
                            "k": str(se_res["_source"]["id"]),
                            "v": str(se_res["_score"]),
                            "n": str(se_res["_source"]["desc"]),
                        }
                    )
                    cc += 1

            ha = {}
            for v in search_arr:
                if v["k"] in ha.keys():
                    continue
                ha[v["k"]] = 1

                name = v["k"]
                content = v["n"]

                # seven_digit_number = str(number).zfill(6)
                pre_name = name.replace(".txt", "")
                # print(seven_digit_number)
                result = re.search(r"\d+", name)
                number = int(result.group())

                with open(
                    search_folder + "/" + pre_name + ".java", "w", encoding="utf-8"
                ) as f:
                    f.write(content)

            # 创建pairs件
            pairs_path = "pairs"
            with open(pairs_path, "w") as file:
                for v in ha.keys():
                    number = v
                    seven_digit_number = number.replace(".txt", "")
                    # seven_digit_number = str(number).zfill(6)

                    file.writelines(
                        os.path.basename(file_path)
                        + " "
                        + seven_digit_number
                        + ".java\n"
                    )
            # 跑程序得到结果
            data = calc.get_check_params(
                pairs_path, upload_folder, search_folder
            )  # 调用类中的add方法
            if data is None or len(data) == 0:
                return jsonify({
                    "status": "success",
                    "message": "根据自主率代码库检索，该代码文件未发现明显复用情况，可放心使用"
                }), 200  # 修改为200状态码表示成功
            else:
                code_main.mymain([upload_folder], [search_folder], data)
                
                # 创建一个线程来处理清理工作
                def cleanup_files():
                    time.sleep(5)  # 如果真的需要延迟，可以在后台线程中执行
                    try:
                        if os.path.exists(upload_folder):
                            util.delete_folder_contents(upload_folder)
                        if os.path.exists(search_folder):
                            util.delete_folder_contents(search_folder)
                    except Exception as e:
                        print(f"清理文件时出错: {e}")
                
                # 启动后台线程进行清理
                threading.Thread(target=cleanup_files).start()
                
                return '', 204  # 立即返回响应，不等待清理完成

    except Exception as e:
        print("❌ 发生异常了！")
        print(f"🔍 异常类型: {type(e).__name__}")
        print(f"📌 异常信息: {e}")
        traceback.print_exc()
        return jsonify({"error": str(e)}), 500









if __name__ == "__main__":
    current_dir = os.path.dirname(os.path.abspath(__file__))
    print(current_dir)
    jar_path = "maven-check.jar"
    jar_path = os.path.join(current_dir, jar_path)
    jvm_path = jpype.getDefaultJVMPath()

    jpype.startJVM(
        jvm_path, "-ea", "-Djava.class.path=%s" % jar_path, convertStrings=False
    )
    ainclass = jpype.JClass("com.shediao.Main")
    calc = ainclass()
    app.run(host="0.0.0.0", port=80, debug=True)

    jpype.shutdownJVM()
    app.mainloop()
