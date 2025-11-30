@echo off
echo ========================================
echo   PDF Super Studio Pro AI
echo ========================================
echo.
echo Starting application...
echo.

cd /d "%~dp0"
call mvn javafx:run

pause
