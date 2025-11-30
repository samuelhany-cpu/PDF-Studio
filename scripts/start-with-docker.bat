@echo off
echo ========================================
echo  PDF Studio - Docker Compose Launcher
echo ========================================
echo.

echo Starting all services with Docker Compose...
docker-compose up -d

echo.
echo ========================================
echo  Services started!
echo ========================================
echo.
echo  - Redis:       http://localhost:6379
echo  - RabbitMQ UI: http://localhost:15672 (guest/guest)
echo  - AI Service:  http://localhost:8081
echo.
echo  View logs:
echo    docker-compose logs -f ai-service
echo.
echo  Stop services:
echo    docker-compose down
echo.
pause
