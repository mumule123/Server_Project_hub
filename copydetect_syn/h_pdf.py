import pdfkit
import os



if __name__ == '__main__':

    pdfkit_options = {
        '--encoding': 'UTF-8'
    }
    confg = pdfkit.configuration(wkhtmltopdf='D:/software/wi/wkhtmltopdf/bin/wkhtmltopdf.exe')
    # 获取HTML文件所在的文件夹路径
    html_files = './htmls/css_test.html'

    # 获取所有HTML文件的路径

    # 设置输出PDF文件的路径
    output_pdf = './mypdf/test.pdf'

    # 将HTML文件批量转换为PDF
    pdfkit.from_file(html_files, output_pdf, options=pdfkit_options,configuration=confg)
