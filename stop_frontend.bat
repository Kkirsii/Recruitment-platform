@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM 招聘平台前端一键停止脚本 (Windows版本)
REM 使用方法: stop_frontend.bat
REM 功能: 停止所有前端服务

title 招聘平台前端停止脚本

echo.
echo 🛑 招聘平台前端停止脚本
echo ================================
echo.

echo 🔍 正在查找并停止所有前端相关进程...

REM 停止Node.js进程（前端开发服务器）
echo 🔄 停止Node.js进程...
for /f "tokens=2" %%i in ('tasklist /fi "imagename eq node.exe" /fo csv ^| findstr /i "vite"') do (
    echo 终止Node.js Vite进程: %%i
    taskkill /f /pid %%i >nul 2>&1
)

REM 停止所有Node.js进程
echo 🔄 停止所有Node.js进程...
taskkill /f /im node.exe >nul 2>&1

REM 停止npm进程
echo 🔄 停止npm进程...
taskkill /f /im npm.cmd >nul 2>&1

REM 停止相关命令行窗口
echo 🔄 停止相关命令行窗口...

REM 停止用户端前端窗口
for /f "tokens=2" %%i in ('tasklist /fi "imagename eq cmd.exe" /fo csv ^| findstr /i "用户端前端"') do (
    echo 终止用户端前端窗口: %%i
    taskkill /f /pid %%i >nul 2>&1
)

REM 停止管理员端前端窗口
for /f "tokens=2" %%i in ('tasklist /fi "imagename eq cmd.exe" /fo csv ^| findstr /i "管理员端前端"') do (
    echo 终止管理员端前端窗口: %%i
    taskkill /f /pid %%i >nul 2>&1
)

REM 停止包含端口5173和5174的进程
echo 🔄 停止端口5173和5174相关进程...
netstat -ano | findstr :5173 >nul 2>&1
if not errorlevel 1 (
    for /f "tokens=5" %%i in ('netstat -ano ^| findstr :5173') do (
        echo 终止端口5173进程: %%i
        taskkill /f /pid %%i >nul 2>&1
    )
)

netstat -ano | findstr :5174 >nul 2>&1
if not errorlevel 1 (
    for /f "tokens=5" %%i in ('netstat -ano ^| findstr :5174') do (
        echo 终止端口5174进程: %%i
        taskkill /f /pid %%i >nul 2>&1
    )
)

REM 等待进程完全停止
echo ⏳ 等待进程完全停止...
timeout /t 3 /nobreak >nul

REM 检查是否还有残留进程
echo 🔍 检查残留进程...
set remaining=0

netstat -ano | findstr :5173 >nul 2>&1
if not errorlevel 1 (
    echo ⚠️  端口5173仍被占用
    set remaining=1
)

netstat -ano | findstr :5174 >nul 2>&1
if not errorlevel 1 (
    echo ⚠️  端口5174仍被占用
    set remaining=1
)

if %remaining%==0 (
    echo ✅ 所有前端服务已成功停止
) else (
    echo ⚠️  部分服务可能仍在运行，请手动检查
)

echo.
echo 🎉 前端停止脚本执行完成
echo ================================
echo 💡 重新启动前端服务: start_frontend.bat
echo ================================
echo.
echo 💡 按任意键退出...
pause >nul
