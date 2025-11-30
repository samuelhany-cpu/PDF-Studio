@echo off
echo ========================================
echo  PDF Studio Microservices Launcher
echo ========================================
echo.

echo [1/4] Starting Redis cache...
docker run -d --name pdf-studio-redis -p 6379:6379 redis:alpine
if errorlevel 1 (
    echo Redis already running or failed to start
    docker start pdf-studio-redis
)
echo ✓ Redis started on port 6379
echo.

echo [2/4] Starting RabbitMQ message queue...
docker run -d --name pdf-studio-rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management-alpine
if errorlevel 1 (
    echo RabbitMQ already running or failed to start
    docker start pdf-studio-rabbitmq
)
echo ✓ RabbitMQ started on ports 5672 (AMQP) and 15672 (Management UI)
echo.

echo [3/4] Waiting for services to be ready...
timeout /t 10 /nobreak > nul
echo ✓ Services ready
echo.

echo [4/4] Starting AI Service...
cd /d "%~dp0\.."
cd ai-service
start "AI Service" mvn spring-boot:run
cd ..
echo ✓ AI Service starting on port 8081
echo.

echo ========================================
echo  All services started!
echo ========================================
echo.
echo  - Redis:       http://localhost:6379
echo  - RabbitMQ:    http://localhost:15672 (guest/guest)
echo  - AI Service:  http://localhost:8081
echo.
echo  Check AI Service health:
echo  curl http://localhost:8081/api/ai/health
echo.
echo  To start main app:
echo  cd .. (if in ai-service)
echo  mvn javafx:run
echo.
pause
