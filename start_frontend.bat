@echo off
chcp 65001 >nul

REM 前端启动脚本
REM 功能: 启动用户端前端和管理员端前端

title 前端启动脚本

echo.
echo 🚀 启动前端服务
echo ================
echo.

REM 检查Node.js
node --version >nul 2>&1
if errorlevel 1 (
    echo ❌ 未找到Node.js，请先安装
    pause
    exit /b 1
)

REM 启动用户端前端
echo 👤 启动用户端前端...
cd rp-frontend
if not exist "node_modules" (
    echo 📦 安装依赖...
    call npm install
)
start "用户端前端" cmd /k "npm run dev"
cd ..

REM 启动管理员端前端
echo 👨‍💼 启动管理员端前端...
cd admin-frontend
if not exist "node_modules" (
    echo 📦 安装依赖...
    call npm install
)
start "管理员端前端" cmd /k "npm run dev"
cd ..

echo.
echo ✅ 前端服务启动完成
echo 📋 访问地址:
echo    🔗 用户端: http://localhost:5173
echo    🔗 管理员端: http://localhost:5174
echo.
pause
