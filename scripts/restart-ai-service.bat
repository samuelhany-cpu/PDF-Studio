@echo off
echo ========================================
echo  Restarting AI Microservice
echo ========================================
echo.

cd ai-service

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
