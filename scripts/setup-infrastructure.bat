@echo off
echo ========================================
echo  PDF Studio - Infrastructure Setup
echo ========================================
echo.

echo This script will help you set up:
echo   1. llama.cpp verification
echo   2. Docker infrastructure
echo   3. AI microservice startup
echo.
pause

echo.
echo ========================================
echo  Step 1: Verifying llama.cpp
echo ========================================
echo.
call test-llama.bat

echo.
echo ========================================
echo  Step 2: Starting Docker Containers
echo ========================================
echo.
echo Starting Redis and RabbitMQ...
docker-compose up -d

echo.
echo Waiting for containers to be ready...
timeout /t 5 /nobreak > nul

echo.
echo Checking container status...
docker ps

echo.
echo Testing Redis connection...
docker exec pdf-redis redis-cli ping

echo.
echo ========================================
echo  Step 3: Starting AI Service
echo ========================================
echo.
echo Please follow these steps manually:
echo.
echo 1. Re-enable Redis in ai-service/src/main/resources/application.yml
echo    - Remove: spring.data.redis.enabled: false
echo    - Remove: spring.autoconfigure.exclude section
echo.
echo 2. Add @EnableCaching back to AIServiceApplication.java
echo.
echo 3. Run: restart-ai-service.bat
echo.
echo ========================================
echo  Docker Containers are Ready!
echo ========================================
echo.
echo See SETUP_GUIDE.md for detailed instructions.
echo.
pause
