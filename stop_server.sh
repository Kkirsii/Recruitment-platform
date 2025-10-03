#!/bin/bash

# 文件服务器停止脚本
# 使用方法: ./stop_server.sh

echo "🛑 停止文件服务器..."

# 查找server进程
SERVER_PIDS=$(ps aux | grep '[p]ython3 file_server.py' | awk '{print $2}')

if [ -z "$SERVER_PIDS" ]; then
    echo "❌ 未找到运行中的文件服务器进程"
    exit 1
fi

echo "📍 找到进程ID: $SERVER_PIDS"

# 停止服务器进程
for pid in $SERVER_PIDS; do
    echo "🔄 正在终止进程 $pid..."
    kill $pid
    
    # 等待进程结束
    sleep 2
    
    # 检查进程是否还在运行
    if ps -p $pid > /dev/null 2>&1; then
        echo "⚠️  进程 $pid 仍在运行，强制终止..."
        kill -9 $pid
        sleep 1
        
        if ps -p $pid > /dev/null 2>&1; then
            echo "❌ 无法终止进程 $pid"
        else
            echo "✅ 进程 $pid 已成功终止"
        fi
    else
        echo "✅ 进程 $pid 已成功终止"
    fi
done

# 检查是否还有残留进程
REMAINING_PIDS=$(ps aux | grep '[p]ython3 file_server.py' | awk '{print $2}')
if [ -n "$REMAINING_PIDS" ]; then
    echo "⚠️  警告: 仍有进程未完全终止: $REMAINING_PIDS"
else
    echo "🎉 所有文件服务器进程已成功停止"
fi
