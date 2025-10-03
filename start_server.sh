#!/bin/bash

# 文件服务器启动脚本
# 使用方法: ./start_server.sh

echo "🚀 启动文件服务器..."

# 检查Python版本
python_version=$(python3 --version 2>/dev/null)
if [ $? -ne 0 ]; then
    echo "❌ 错误: 未找到Python3，请先安装Python3"
    exit 1
fi

# 显示Python版本
echo "📋 Python版本: $python_version"

# 检查是否安装了PyYAML
python3 -c "import yaml" 2>/dev/null
if [ $? -ne 0 ]; then
    echo "📦 正在安装PyYAML..."
    pip3 install pyyaml
    if [ $? -ne 0 ]; then
        echo "❌ 错误: 无法安装PyYAML，请手动安装: pip3 install pyyaml"
        exit 1
    fi
fi

# 给Python脚本添加执行权限
chmod +x file_server.py

# 检查配置文件
if [ ! -f "config.yaml" ]; then
    echo "⚠️  警告: 未找到config.yaml文件，将使用默认配置"
fi

# 显示当前工作目录
echo "📁 当前工作目录: $(pwd)"

# 显示文件列表
echo "📋 可用文件列表:"
ls -la | grep -E "^-" | awk '{print "  - " $NF}'

# 启动服务器（后台运行）
echo "🌟 启动服务器中（后台运行）..."
echo "📍 访问地址: http://localhost:8080"
echo "🔍 获取文件示例: http://localhost:8080?file=config.yaml"
echo "📝 日志文件: log.txt"
echo "⏹️  要停止服务器，请使用: kill \$(ps aux | grep '[p]ython3 file_server.py' | awk '{print \$2}')"
echo ""

# 后台运行Python服务器，将日志输出到log.txt
nohup python3 file_server.py > log.txt 2>&1 &

# 获取进程ID
SERVER_PID=$!
echo "✅ 服务器已启动，进程ID: $SERVER_PID"
echo "📝 日志正在写入: log.txt"
echo "🔍 查看实时日志: tail -f log.txt"
