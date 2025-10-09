#!/usr/bin/env python3
"""
文件服务器脚本
功能：通过HTTP接口接收参数，返回指定文件
运行环境：Linux
"""

import os
import yaml
import json
from http.server import HTTPServer, BaseHTTPRequestHandler
from urllib.parse import urlparse, parse_qs
import mimetypes
import logging
from pathlib import Path

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('file_server.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

class FileRequestHandler(BaseHTTPRequestHandler):
    """HTTP请求处理器"""
    
    def __init__(self, config, *args, **kwargs):
        self.config = config
        super().__init__(*args, **kwargs)
    
    def do_GET(self):
        """处理GET请求"""
        try:
            # 解析URL
            parsed_url = urlparse(self.path)
            query_params = parse_qs(parsed_url.query)
            
            # 获取文件参数
            file_path = query_params.get('file', [None])[0]
            
            if not file_path:
                self.send_error_response(400, "缺少file参数")
                return
            
            # 构建文件完整路径
            full_path = os.path.join(os.getcwd(), file_path)
            
            # 验证文件路径安全性
            if not self.is_safe_path(full_path):
                self.send_error_response(403, "文件路径不安全")
                return
            
            # 检查文件是否存在
            if not os.path.exists(full_path):
                self.send_error_response(404, f"文件不存在: {file_path}")
                return
            
            # 发送文件
            self.send_file(full_path)
            
        except Exception as e:
            logger.error(f"处理请求时出错: {e}")
            self.send_error_response(500, f"服务器内部错误: {str(e)}")
    
    def do_POST(self):
        """处理POST请求"""
        try:
            # 检查Content-Type
            content_type = self.headers.get('Content-Type', '')
            
            if 'multipart/form-data' in content_type:
                # 处理文件上传
                self.handle_file_upload()
            elif 'application/json' in content_type:
                # 处理JSON请求（文件下载）
                self.handle_json_request()
            else:
                # 处理表单数据（文件下载）
                self.handle_form_request()
                
        except Exception as e:
            logger.error(f"处理POST请求时出错: {e}")
            self.send_error_response(500, f"服务器内部错误: {str(e)}")
    
    def handle_file_upload(self):
        """处理文件上传"""
        try:
            # 获取Content-Length
            content_length = int(self.headers.get('Content-Length', 0))
            if content_length == 0:
                self.send_error_response(400, "请求体为空")
                return
            
            # 读取原始数据
            post_data = self.rfile.read(content_length)
            
            # 解析multipart数据
            boundary = None
            content_type = self.headers.get('Content-Type', '')
            if 'boundary=' in content_type:
                boundary = '--' + content_type.split('boundary=')[1]
            
            if not boundary:
                self.send_error_response(400, "无效的multipart数据")
                return
            
            # 解析multipart数据
            parts = post_data.split(boundary.encode())
            file_data = None
            destination = None
            filename = None
            
            for part in parts[1:-1]:  # 跳过第一个和最后一个部分
                if b'\r\n\r\n' in part:
                    header, data = part.split(b'\r\n\r\n', 1)
                    header_str = header.decode('utf-8', errors='ignore')
                    
                    # 解析filename
                    if 'filename=' in header_str:
                        filename = header_str.split('filename=')[1].strip('"')
                    
                    # 处理文件数据
                    if filename and not file_data:
                        file_data = data
                    elif 'name="destination"' in header_str:
                        destination = data.decode('utf-8').strip()
            
            if not file_data or not destination:
                self.send_error_response(400, "缺少文件数据或目标路径")
                return
            
            # 验证目标路径安全性
            if not self.is_safe_path(destination):
                self.send_error_response(403, "目标路径不安全")
                return
            
            # 构建完整的目标路径
            full_destination = os.path.join(os.getcwd(), destination)
            
            # 确保目录存在
            os.makedirs(os.path.dirname(full_destination), exist_ok=True)
            
            # 写入文件
            with open(full_destination, 'wb') as f:
                f.write(file_data)
            
            # 返回成功响应
            success_response = {
                "success": True,
                "message": f"文件上传成功",
                "destination": destination,
                "filename": filename,
                "size": len(file_data)
            }
            
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()
            
            json_response = json.dumps(success_response, ensure_ascii=False).encode('utf-8')
            self.wfile.write(json_response)
            
            logger.info(f"文件上传成功: {destination} ({len(file_data)} bytes)")
            
        except Exception as e:
            logger.error(f"文件上传失败: {e}")
            self.send_error_response(500, f"文件上传失败: {str(e)}")
    
    def handle_json_request(self):
        """处理JSON请求（文件下载）"""
        try:
            content_length = int(self.headers['Content-Length'])
            post_data = self.rfile.read(content_length)
            
            data = json.loads(post_data.decode('utf-8'))
            file_path = data.get('file')
            
            if not file_path:
                self.send_error_response(400, "缺少file参数")
                return
            
            # 构建文件完整路径
            full_path = os.path.join(os.getcwd(), file_path)
            
            # 验证文件路径安全性
            if not self.is_safe_path(full_path):
                self.send_error_response(403, "文件路径不安全")
                return
            
            # 检查文件是否存在
            if not os.path.exists(full_path):
                self.send_error_response(404, f"文件不存在: {file_path}")
                return
            
            # 发送文件
            self.send_file(full_path)
            
        except json.JSONDecodeError:
            self.send_error_response(400, "无效的JSON数据")
        except Exception as e:
            logger.error(f"处理JSON请求失败: {e}")
            self.send_error_response(500, f"服务器内部错误: {str(e)}")
    
    def handle_form_request(self):
        """处理表单请求（文件下载）"""
        try:
            content_length = int(self.headers['Content-Length'])
            post_data = self.rfile.read(content_length)
            
            post_str = post_data.decode('utf-8')
            if 'file=' in post_str:
                file_path = post_str.split('file=')[1].split('&')[0]
            else:
                self.send_error_response(400, "缺少file参数")
                return
            
            if not file_path:
                self.send_error_response(400, "缺少file参数")
                return
            
            # 构建文件完整路径
            full_path = os.path.join(os.getcwd(), file_path)
            
            # 验证文件路径安全性
            if not self.is_safe_path(full_path):
                self.send_error_response(403, "文件路径不安全")
                return
            
            # 检查文件是否存在
            if not os.path.exists(full_path):
                self.send_error_response(404, f"文件不存在: {file_path}")
                return
            
            # 发送文件
            self.send_file(full_path)
            
        except Exception as e:
            logger.error(f"处理表单请求失败: {e}")
            self.send_error_response(500, f"服务器内部错误: {str(e)}")
    
    def send_file(self, file_path):
        """发送文件内容"""
        try:
            # 获取文件的MIME类型
            mime_type, _ = mimetypes.guess_type(file_path)
            if mime_type is None:
                mime_type = 'application/octet-stream'
            
            # 读取文件内容
            with open(file_path, 'rb') as f:
                file_content = f.read()
            
            # 发送响应头
            self.send_response(200)
            self.send_header('Content-Type', mime_type)
            self.send_header('Content-Length', str(len(file_content)))
            self.send_header('Content-Disposition', f'attachment; filename="{os.path.basename(file_path)}"')
            self.end_headers()
            
            # 发送文件内容
            self.wfile.write(file_content)
            
            logger.info(f"成功发送文件: {file_path}")
            
        except Exception as e:
            logger.error(f"发送文件时出错: {e}")
            self.send_error_response(500, f"读取文件失败: {str(e)}")
    
    def send_error_response(self, code, message):
        """发送错误响应"""
        error_response = {
            "error": True,
            "code": code,
            "message": message
        }
        
        self.send_response(code)
        self.send_header('Content-Type', 'application/json')
        self.end_headers()
        
        json_response = json.dumps(error_response, ensure_ascii=False).encode('utf-8')
        self.wfile.write(json_response)
    
    def is_safe_path(self, path):
        """检查文件路径是否安全（防止目录遍历攻击）"""
        try:
            # 获取当前工作目录
            base_dir = os.getcwd()
            
            # 解析路径
            abs_path = os.path.abspath(path)
            base_dir = os.path.abspath(base_dir)
            
            # 检查路径是否在工作目录内
            return abs_path.startswith(base_dir)
            
        except Exception:
            return False
    
    def list_directory(self):
        """列出当前目录的文件"""
        try:
            files = []
            directories = []
            
            for item in os.listdir('.'):
                item_path = os.path.join('.', item)
                if os.path.isdir(item_path):
                    directories.append(item)
                else:
                    files.append(item)
            
            response = {
                "files": files,
                "directories": directories,
                "current_directory": os.getcwd()
            }
            
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()
            
            json_response = json.dumps(response, ensure_ascii=False).encode('utf-8')
            self.wfile.write(json_response)
            
        except Exception as e:
            self.send_error_response(500, f"列出目录失败: {str(e)}")
    
    def log_message(self, format, *args):
        """自定义日志格式"""
        logger.info(f"{self.client_address[0]} - {format % args}")

def load_config():
    """加载配置文件"""
    try:
        if os.path.exists('config.yaml'):
            with open('config.yaml', 'r', encoding='utf-8') as f:
                config = yaml.safe_load(f)
                logger.info("配置文件加载成功")
                return config
        else:
            logger.warning("未找到config.yaml文件，使用默认配置")
            return get_default_config()
    except Exception as e:
        logger.error(f"加载配置文件失败: {e}")
        return get_default_config()

def get_default_config():
    """默认配置"""
    return {
        'host': '0.0.0.0',
        'port': 8081,
        'allowed_extensions': ['.txt', '.log', '.json', '.yaml', '.yml', '.csv'],
        'max_file_size': 104857600,  # 100MB
        'enable_cors': True,
        'log_level': 'INFO'
    }

def create_config_file():
    """创建默认配置文件"""
    config = get_default_config()
    with open('config.yaml', 'w', encoding='utf-8') as f:
        yaml.dump(config, f, default_flow_style=False, allow_unicode=True)
    logger.info("已创建默认配置文件 config.yaml")

def main():
    """主函数"""
    # 加载配置
    config = load_config()
    
    # 如果没有配置文件，创建一个
    if not os.path.exists('config.yaml'):
        create_config_file()
        config = load_config()
    
    host = config.get('host', '0.0.0.0')
    port = config.get('port', 8081)
    
    logger.info(f"启动文件服务器 - 地址: {host}:{port}")
    logger.info(f"当前工作目录: {os.getcwd()}")
    
    # 创建自定义的请求处理器
    def handler(*args, **kwargs):
        return FileRequestHandler(config, *args, **kwargs)
    
    # 启动HTTP服务器
    try:
        with HTTPServer((host, port), handler) as httpd:
            logger.info(f"服务器启动成功！")
            logger.info(f"访问地址: http://{host}:{port}")
            logger.info(f"获取文件示例: http://{host}:{port}?file=filename.txt")
            logger.info("按 Ctrl+C 停止服务器")
            
            httpd.serve_forever()

    except KeyboardInterrupt:
        logger.info("服务器已停止")
    except Exception as e:
        logger.error(f"服务器启动失败: {e}")

if __name__ == "__main__":
    main()
