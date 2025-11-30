@echo off
echo ========================================
echo  Starting Docker Infrastructure
echo ========================================
echo.

echo Step 1: Checking Docker status...
docker ps >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Docker Desktop is not running!
    echo.
    echo Please:
    echo   1. Open Docker Desktop application
    echo   2. Wait for it to fully start
    echo   3. Run this script again
    echo.
    pause
    exit /b 1
)

echo [OK] Docker is running!
echo.

echo Step 2: Starting Redis and RabbitMQ containers...
docker-compose up -d

echo.
echo Step 3: Waiting for containers to be ready...
timeout /t 5 /nobreak > nul

echo.
echo Step 4: Checking container status...
docker ps

echo.
echo Step 5: Testing Redis connection...
docker exec pdf-redis redis-cli ping

echo.
echo ========================================
echo  Infrastructure is Ready!
echo ========================================
echo.
echo Now restart the AI service with: restart-ai-service.bat
echo.
pause
