@echo off
title ShopVerse - Spring Boot Backend (Port 8080)
color 0A
echo ============================================================================
echo   SHOPVERSE E-COMMERCE - SPRING BOOT 3.x BACKEND LAUNCHER
echo ============================================================================
echo.

cd /d "%~dp0backend"

:: Locate Java
set "JAVA_HOME=C:\Program Files\Java\jdk-25.0.2"
set "JAVA_EXE="
if exist "C:\Program Files\Common Files\Oracle\Java\javapath\java.exe" (
    set "JAVA_EXE=C:\Program Files\Common Files\Oracle\Java\javapath\java.exe"
) else if exist "C:\Program Files\Java\jdk-25.0.2\bin\java.exe" (
    set "JAVA_EXE=C:\Program Files\Java\jdk-25.0.2\bin\java.exe"
)

:: Locate Maven
set "MVN_CMD="
if exist "%USERPROFILE%\.vscode\extensions\oracle.oracle-java-27.0.0\nbcode\java\maven\bin\mvn.cmd" (
    set "MVN_CMD=%USERPROFILE%\.vscode\extensions\oracle.oracle-java-27.0.0\nbcode\java\maven\bin\mvn.cmd"
) else if exist "%~dp0backend\apache-maven-3.9.9\bin\mvn.cmd" (
    set "MVN_CMD=%~dp0backend\apache-maven-3.9.9\bin\mvn.cmd"
) else if exist "%~dp0backend\mvnw.cmd" (
    set "MVN_CMD=%~dp0backend\mvnw.cmd"
) else (
    for %%X in (mvn.cmd mvn) do (
        if not defined MVN_CMD (
            set "MVN_CMD=%%~$PATH:X"
        )
    )
)

if defined MVN_CMD (
    echo [OK] Found Maven at: "%MVN_CMD%"
    echo.
    echo [*] Starting Spring Boot with in-memory Oracle-compatible mode (Port 8080)...
    echo [*] Endpoints:
    echo     - REST APIs:   http://localhost:8080/api/products
    echo     - H2 Console:  http://localhost:8080/h2-console
    echo.
    call "%MVN_CMD%" spring-boot:run -Dspring-boot.run.profiles=local
) else (
    echo [!] Maven command line executable was not found in PATH.
    echo.
    echo To run the backend:
    echo   Option 1: Open the 'backend' folder in VS Code or IntelliJ IDEA and run MarketplaceApplication.java
    echo   Option 2: Install Apache Maven and add its 'bin' directory to your System PATH.
    echo.
)

pause
