# 文件服务器

一个简单的Python HTTP文件服务器，用于Linux环境下的文件访问服务。

## 功能特性

- 🌐 HTTP服务器（GET/POST请求）
- 📁 返回当前目录下的指定文件
- 📤 文件上传功能（支持multipart/form-data）
- 🔒 安全的文件路径验证（防止目录遍历攻击）
- 📋 配置文件支持（config.yaml）
- 📝 完整的日志记录
- 🔧 灵活的配置选项

## 安装要求

```bash
# Python 3.6+ 
pip install pyyaml
```

或者使用requirements.txt：
```bash
pip install -r requirements.txt
```

## 使用方法

### 1. 配置服务器

编辑 `config.yaml` 文件：

```yaml
host: '0.0.0.0'  # 监听地址
port: 8080       # 端口号
```

### 2. 启动服务器

```bash
# 给脚本添加执行权限
chmod +x file_server.py

# 启动服务器
python3 file_server.py
```

### 3. API使用方法

#### GET请求（推荐）

```bash
# 获取文件
curl "http://localhost:8080?file=example.txt"

# 或者使用浏览器
http://localhost:8080?file=example.txt
```

#### POST请求

##### 文件下载（JSON格式）
```bash
curl -X POST http://localhost:8080 \
  -H "Content-Type: application/json" \
  -d '{"file": "example.txt"}'

# 表单格式
curl -X POST http://localhost:8080 \
  -d "file=example.txt"
```

##### 文件上传（multipart/form-data）
```bash
# 上传文件到指定位置
curl -X POST http://localhost:8080 \
  -F "file=@/path/to/local/file.txt" \
  -F "destination=uploads/remote_file.txt"

# 上传到子目录（目录会自动创建）
curl -X POST http://localhost:8080 \
  -F "file=@document.pdf" \
  -F "destination=documents/important.pdf"
```

### 4. API响应格式

#### 成功响应
##### 文件下载
直接返回文件内容，Content-Type根据文件类型自动设置

##### 文件上传
```json
{
  "success": true,
  "message": "文件上传成功",
  "destination": "uploads/file.txt",
  "filename": "file.txt",
  "size": 1024
}
```

#### 错误响应
```json
{
  "error": true,
  "code": 404,
  "message": "文件不存在: example.txt"
}
```

## 安全特性

- ✅ 文件路径验证，防止目录遍历攻击
- ✅ 只能访问当前工作目录下的文件
- ✅ 配置文件支持文件扩展名限制
- ✅ 文件大小限制保护
- ✅ 完整的错误处理和日志记录

## 配置选项

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `host` | `'0.0.0.0'` | 服务器监听地址 |
| `port` | `8080` | 服务器端口 |
| `allowed_extensions` | `[]` | 允许的文件扩展名列表 |
| `max_file_size` | `104857600` | 最大文件大小（字节） |
| `enable_cors` | `true` | 启用CORS支持 |
| `log_level` | `'INFO'` | 日志级别 |

## 使用示例

### 示例1：获取配置文件
```bash
curl "http://localhost:8080?file=config.yaml"
```

### 示例2：获取日志文件
```bash
curl "http://localhost:8080?file=file_server.log"
```

### 示例3：获取Python脚本
```bash
curl "http://localhost:8080?file=file_server.py"
```

### 示例4：上传文件
```bash
# 上传文本文件
curl -X POST http://localhost:8080 \
  -F "file=@README.md" \
  -F "destination=docs/readme_backup.md"

# 上传图片文件
curl -X POST http://localhost:8080 \
  -F "file=@image.jpg" \
  -F "destination=images/uploaded_image.jpg"

# 上传到多层目录
curl -X POST http://localhost:8080 \
  -F "file=@project.zip" \
  -F "destination=archives/2024/project.zip"
```

## 目录结构

```
your-project/
├── file_server.py    # 主服务器脚本
├── config.yaml       # 配置文件
├── README.md         # 说明文档
├── file_server.log   # 日志文件（运行后生成）
└── your-files/       # 你的其他文件...
```

## 系统要求

- Linux操作系统
- Python 3.6+
- PyYAML库

## 注意事项

1. 服务器只能访问当前工作目录下的文件
2. 确保有足够的权限读取目标文件
3. 大文件传输可能较慢，建议设置合适的超时时间
4. 生产环境建议使用反向代理（如nginx）

## 故障排除

### 问题1：端口被占用
```bash
# 查看端口占用
netstat -tlnp | grep 8080

# 修改config.yaml中的port配置
port: 8081
```

### 问题2：文件访问权限不足
```bash
# 检查文件权限
ls -la your_file.txt

# 给予读取权限
chmod 644 your_file.txt
```

### 问题3：Python模块缺失
```bash
# 安装PyYAML
pip3 install pyyaml
```

## 扩展功能

如果需要更多功能，可以考虑以下扩展：

- 🔐 用户认证
- 📊 文件访问统计
- 🗜️ 文件压缩传输
- 🔄 文件缓存机制
- 📱 Web管理界面

## 许可证

MIT License
