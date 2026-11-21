@echo off
echo =======================================================
echo Pushing ShopVerse Project to GitHub...
echo Repository: https://github.com/AnshikaJaiswal201/java-online-market-place.git
echo =======================================================
cd /d "%~dp0"
git push -u origin main
echo.
if %ERRORLEVEL% EQU 0 (
    echo =======================================================
    echo SUCCESS! Project successfully uploaded to GitHub!
    echo =======================================================
) else (
    echo =======================================================
    echo Push failed. Please make sure the repo 'java-online-market-place'
    echo is created on https://github.com/new
    echo =======================================================
)
pause
