@echo off
echo ========================================
echo   PDF Super Studio Pro AI
echo ========================================
echo.
echo Starting application...
echo.

REM Go to project root (parent of scripts folder)
cd /d "%~dp0\.."
echo Running from: %cd%
echo.

call mvn javafx:run

pause
