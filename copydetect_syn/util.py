import re
import os
import glob
from elasticsearch import Elasticsearch

# 建立索引
def datadeal(folder_path)->[]:
    ans_arry = []
    file_paths = glob.glob(os.path.join(folder_path, '*.txt'))
    for file_path in file_paths:
        file_name = os.path.basename(file_path)  # 获取文件名
        with open(file_path, 'r',encoding='utf-8') as file:
            code = file.read()  # 读取代码内容
            # 处理代码，例如将代码转换为字符串
            code_string = code.replace('\n', '')  # 示例：将换行符转换为
            words = re.findall(r'\w+', code_string)
            ans_dict = {}
            ids = int(re.search(r'source-document(\d+)', file_name.split(sep='.')[0]).group(1))
            ans_dict["id"] = ids
            words_string = ""
            for v in words:
                words_string += v
                words_string += " "
            ans_dict["desc"] = words_string
            ans_arry.append(ans_dict)
    return ans_arry

def getoverlap_datadeal(folder_path,overlap,piece,index_name):
    es = Elasticsearch([{'host': '10.130.71.10', 'port': 30434, 'scheme': 'http'}])
    ans_arry = []
    file_paths = glob.glob(os.path.join(folder_path, '*.txt'))
    auto_ids = 1
    for file_path in file_paths:
        file_name = os.path.basename(file_path)  # 获取文件名
        with open(file_path, 'r', encoding='utf-8') as file:
            code = file.read()  # 读取代码内容
            # 处理代码，例如将代码转换为字符串
            code_string = code.replace('\n', '')  # 示例：将换行符转换为
            response = es.indices.analyze(
                index=index_name,
                body=
                {
                    "text": code_string,
                    "analyzer": "standard"
                }

            )
            words = [token['token'] for token in response['tokens']]
            ids = int(re.search(r'source-document(\d+)', file_name.split(sep='.')[0]).group(1))

            # 词的长度
            le = len(words)
            i = 0

            #逻辑实现分块和overlap的逻辑
            while i < le:
                cnt = 0
                words_string = ""
                ans_dict = {}
                ans_dict["id"] = ids
                while cnt < piece and i + cnt < le:
                    words_string += words[i+cnt]
                    words_string += " "
                    cnt += 1
                ans_dict["desc"] = words_string
                i += overlap
                ans_arry.append(ans_dict)

    return ans_arry

def get_arr(folder_path)->[]:
    ans_arry = []
    file_paths = glob.glob(os.path.join(folder_path, '*.java'))
    for file_path in file_paths:
        file_name = os.path.basename(file_path)  # 获取文件名
        with open(file_path, 'r', encoding='utf-8') as file:
            code = file.read()  # 读取代码内容
            # 处理代码，例如将代码转换为字符串
            code_string = code.replace('\n', '')  # 示例：将换行符转换为
            words = re.findall(r'\w+', code_string)
            ans_arry.append(words)

    return ans_arry

#给一个文件把他返回单词
def get_string_gram(file_path):
    ans_arry = []
    with open(file_path, 'r', encoding='utf-8') as file:
        code = file.read()  # 读取代码内容
        # 处理代码，例如将代码转换为字符串
        code_string = code.replace('\n', '')  # 示例：将换行符转换为
        words = re.findall(r'\w+', code_string)
        ans_arry = words

    return  ans_arry

def delete_folder_contents(folder_path):
    try:
        for root, dirs, files in os.walk(folder_path, topdown=False):
            for file in files:
                file_path = os.path.join(root, file)
                os.remove(file_path)
            for dir in dirs:
                dir_path = os.path.join(root, dir)
                os.rmdir(dir_path)
    except Exception as e:
        print(f"删除文件夹内容时出错：{e}")

def get_string(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        code = file.read()  # 读取代码内容
        code_string = code.replace('\n', '')  #

        file.close()
        return code_string


if __name__ == '__main__':
    # file_flold = './data/bigdata/'
    # datadeal(file_flold)
    s = 'ni ha a'
    print(len(re.findall(r'\w+',s)))