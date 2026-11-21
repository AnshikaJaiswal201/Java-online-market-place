@echo off
title ShopVerse - Angular 14 Frontend (Port 4200)
color 0B
echo ============================================================================
echo   SHOPVERSE E-COMMERCE - ANGULAR 14 FRONTEND LAUNCHER
echo ============================================================================
echo.

cd /d "%~dp0frontend"

:: Check if Node / NPM are in PATH, otherwise append local NVM path
where node >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    if exist "C:\nvm4w\nodejs\node.exe" (
        echo [*] Adding Node.js runtime from C:\nvm4w\nodejs to current session...
        set "PATH=C:\nvm4w\nodejs;%PATH%"
    )
)

echo [*] Checking Node.js version:
call node -v
echo [*] Checking NPM version:
call npm -v
echo.

:: Check node_modules
if not exist "node_modules\" (
    echo [*] Installing node dependencies (first-time setup, please wait)...
    call npm install --legacy-peer-deps
    if %ERRORLEVEL% NEQ 0 (
        echo [ERROR] Failed to install npm dependencies.
        pause
        exit /b %ERRORLEVEL%
    )
)

echo.
echo [*] Starting Angular development server on http://localhost:4200...
echo.
call npm start
pause
