@echo off
echo ========================================
echo  Starting PDF Studio with Microservices
echo ========================================
echo.

echo Step 1: Starting AI Microservice...
start "AI Microservice" cmd /c "cd /d F:\PDF Studio\ai-service && mvn spring-boot:run"

echo Waiting 10 seconds for microservice to start...
timeout /t 10 /nobreak > nul

echo.
echo Step 2: Starting Main JavaFX Application...
cd /d "F:\PDF Studio"
mvn javafx:run

pause
