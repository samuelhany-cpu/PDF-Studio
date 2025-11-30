@echo off
echo ========================================
echo  Restarting AI Microservice
echo ========================================
echo.

REM Go to project root, then to ai-service
cd /d "%~dp0\.."
cd ai-service
echo Current directory: %cd%
echo.

echo [1/2] Compiling updated code...
call mvn clean compile
if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo [2/2] Starting AI service...
echo.

call mvn spring-boot:run

pause
