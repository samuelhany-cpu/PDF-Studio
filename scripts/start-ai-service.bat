@echo off
echo ========================================
echo  Starting AI Microservice
echo ========================================
echo.

cd ai-service
echo Current directory: %cd%
echo.

echo Starting AI service on port 8081...
echo.

mvn spring-boot:run

pause
