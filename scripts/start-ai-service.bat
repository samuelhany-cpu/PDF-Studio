@echo off
echo ========================================
echo  Starting AI Microservice
echo ========================================
echo.

REM Go to project root, then to ai-service
cd /d "%~dp0\.."
cd ai-service
echo Current directory: %cd%
echo.

echo Starting AI service on port 8081...
echo.

mvn spring-boot:run

pause
