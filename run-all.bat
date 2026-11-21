@echo off
title ShopVerse - Full Stack Launcher
color 0E
echo ============================================================================
echo   SHOPVERSE E-COMMERCE - FULL STACK RUNNER (LOCALHOST)
echo ============================================================================
echo.
echo [*] Launching Spring Boot Backend in a separate window (Port 8080)...
start "ShopVerse Backend (8080)" "%~dp0run-backend.bat"

echo [*] Launching Angular 14 Frontend in a separate window (Port 4200)...
start "ShopVerse Frontend (4200)" "%~dp0run-frontend.bat"

echo.
echo [*] Servers are booting up!
echo     - Backend API:    http://localhost:8080/api/products
echo     - Frontend App:   http://localhost:4200
echo.
echo Waiting 5 seconds before opening your browser...
timeout /t 5 >nul
start http://localhost:4200
