# 🚀 Complete Setup Guide - Docker + llama.cpp + Microservices

## Current Status ✅
- ✅ AI Microservice created and compiled
- ✅ Service runs successfully on port 8081
- ✅ GGUF model detected at `F:/PDF Studio/models/Llama-3.2-3B-Instruct-Q6_K_L_2.gguf`
- ✅ Docker Desktop installed
- ✅ llama.cpp downloaded: `llama-b7067-bin-win-cuda-12.4-x64.zip` (CUDA 12.4 version)
- ⚠️ llama.cpp needs extraction
- ⚠️ Docker containers need to be started
- ⚠️ Redis caching currently disabled

---

## 📦 Step 1: Install llama.cpp (5 minutes)

### Extract llama.cpp
1. Locate your download: `llama-b7067-bin-win-cuda-12.4-x64.zip`
2. Extract it to: **`F:\PDF Studio\llama.cpp\`**
3. After extraction, you should have:
   ```
   F:\PDF Studio\llama.cpp\
   ├── llama-cli.exe          ← Command-line interface
   ├── llama-server.exe       ← HTTP server
   ├── cudart64_12.dll        ← CUDA runtime (GPU acceleration)
   ├── cublas64_12.dll        ← CUDA BLAS library
   └── ... (other files)
   ```

### Verify Installation
Open a command prompt in `F:\PDF Studio\` and run:
```cmd
test-llama.bat
```

**Expected output:**
```
[OK] llama-cli.exe found!
Location: F:\PDF Studio\llama.cpp\llama-cli.exe

llama-cli version: b7067 (built with CUDA 12.4)

[OK] GGUF model found!
Location: F:\PDF Studio\models\Llama-3.2-3B-Instruct-Q6_K_L_2.gguf

Everything is ready!
```

---

## 🐳 Step 2: Start Docker Infrastructure (3 minutes)

### Start Redis and RabbitMQ
Open a command prompt in `F:\PDF Studio\` and run:
```cmd
start-with-docker.bat
```

Or manually:
```cmd
docker-compose up -d
```

### Verify Docker Containers
Check that both containers are running:
```cmd
docker ps
```

**Expected output:**
```
CONTAINER ID   IMAGE          STATUS         PORTS
abc123...      redis:7        Up 10 seconds  0.0.0.0:6379->6379/tcp
def456...      rabbitmq:3     Up 10 seconds  0.0.0.0:5672->5672/tcp, 15672/tcp
```

### Test Redis Connection
```cmd
docker exec -it pdf-redis redis-cli ping
```
**Expected:** `PONG`

---

## ⚡ Step 3: Re-enable Redis Caching (2 minutes)

### Update Configuration
Edit **`ai-service/src/main/resources/application.yml`**:

**Remove these lines:**
```yaml
spring:
  data:
    redis:
      enabled: false  # ← DELETE THIS LINE
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration  # ← DELETE THIS LINE
```

### Update Application Class
Edit **`ai-service/src/main/java/com/pdfstudio/ai/AIServiceApplication.java`**:

**Add back the annotation:**
```java
@SpringBootApplication
@EnableCaching  // ← ADD THIS BACK
public class AIServiceApplication {
```

### Update Service Methods
Edit **`ai-service/src/main/java/com/pdfstudio/ai/service/AIOperationsService.java`**:

**Add back caching annotations:**
```java
import org.springframework.cache.annotation.Cacheable;

// Add to imports, then:

@Cacheable(value = "summaries", key = "#request.content.hashCode()")
public SummarizeResponse summarize(SummarizeRequest request) {

@Cacheable(value = "chats", key = "#request.message + '_' + #request.context.orElse('no-context')")
public ChatResponse chat(ChatRequest request) {
```

---

## 🎯 Step 4: Start AI Service with Full Features (1 minute)

### Restart the Service
```cmd
restart-ai-service.bat
```

### Check Service Health
Open a browser or PowerShell:
```powershell
Invoke-RestMethod http://localhost:8081/api/ai/health | ConvertTo-Json
```

**Expected output:**
```json
{
  "status": "UP",
  "modelLoaded": true,
  "modelPath": "F:/PDF Studio/models/Llama-3.2-3B-Instruct-Q6_K_L_2.gguf",
  "modelExists": true,
  "llamaCppFound": true,
  "llamaCppPath": "F:/PDF Studio/llama.cpp/llama-cli.exe"
}
```

---

## 🧪 Step 5: Test Real AI Inference (2 minutes)

### Test Document Summarization
```powershell
$body = @{
    content = "This is a long document about machine learning. It covers neural networks, deep learning architectures, and various optimization techniques used in modern AI systems. The document explains how gradient descent works and why it's important for training models."
    filename = "test.pdf"
    maxLength = 100
} | ConvertTo-Json

Invoke-RestMethod -Uri http://localhost:8081/api/ai/summarize `
    -Method POST `
    -Body $body `
    -ContentType "application/json"
```

**Expected output (with REAL AI response):**
```json
{
  "summary": "The document discusses machine learning fundamentals, including neural networks and optimization methods like gradient descent.",
  "processingTimeMs": 3456,
  "cached": false,
  "modelUsed": "Llama-3.2-3B-Instruct"
}
```

### Test Chat Feature
```powershell
$body = @{
    message = "What is the capital of France?"
    context = "General knowledge"
} | ConvertTo-Json

Invoke-RestMethod -Uri http://localhost:8081/api/ai/chat `
    -Method POST `
    -Body $body `
    -ContentType "application/json"
```

### Test Caching (Run Same Request Twice)
Run the summarization request again. The second time should be MUCH faster:
- **First request**: ~3-5 seconds (actual AI inference)
- **Second request**: ~50ms (from Redis cache)

Check the `cached` field in the response!

---

## 📊 Step 6: Monitor Performance

### Check Service Metrics
```powershell
Invoke-RestMethod http://localhost:8081/api/ai/status
```

### View Docker Logs
```cmd
# Redis logs
docker logs pdf-redis

# RabbitMQ logs
docker logs pdf-rabbitmq
```

### Monitor AI Service Logs
The service logs are displayed in the console. Look for:
- ✅ `Model loaded successfully`
- ✅ `llama.cpp found at: F:/PDF Studio/llama.cpp/llama-cli.exe`
- ✅ `Cache hit for summary request` (on repeat requests)
- ✅ `Generated text in XXXXms`

---

## 🎉 Success Criteria

After completing all steps, you should have:

- ✅ llama.cpp CUDA version installed and working
- ✅ Docker containers (Redis + RabbitMQ) running
- ✅ AI microservice running on port 8081
- ✅ Real LLaMA 3.2 3B inference working
- ✅ GPU acceleration via CUDA 12.4
- ✅ Redis caching reducing response times
- ✅ Health endpoint showing `status: UP`
- ✅ Summary and chat endpoints returning AI-generated content

---

## 🔧 Troubleshooting

### llama.cpp Not Found After Extraction
- Verify exact path: `F:\PDF Studio\llama.cpp\llama-cli.exe`
- Run `test-llama.bat` to diagnose
- Check Windows path separators (backslash)

### Docker Containers Won't Start
```cmd
# Check Docker Desktop is running
docker --version

# View error logs
docker-compose logs

# Restart Docker Desktop
# Then try: docker-compose up -d
```

### Redis Connection Refused
```cmd
# Check container status
docker ps -a

# Restart Redis container
docker restart pdf-redis
```

### GPU/CUDA Not Being Used
- Verify CUDA 12.4 installed on your system
- Check NVIDIA drivers updated
- llama.cpp will fall back to CPU if GPU unavailable (still works!)

### Slow Inference Times
- **First run**: Expected (model loading + inference)
- **Subsequent runs**: Should be faster (cached)
- **GPU vs CPU**: CPU inference will be slower (10-30 seconds vs 2-5 seconds)

---

## 📝 Quick Reference Commands

| Task | Command |
|------|---------|
| Test llama.cpp | `test-llama.bat` |
| Start Docker | `docker-compose up -d` |
| Stop Docker | `docker-compose down` |
| Start AI Service | `start-ai-service.bat` |
| Restart AI Service | `restart-ai-service.bat` |
| Check Health | `Invoke-RestMethod http://localhost:8081/api/ai/health` |
| View Docker Status | `docker ps` |
| View Redis Logs | `docker logs pdf-redis` |

---

## 🎯 Next Steps After Setup

1. **Performance Tuning**: Adjust llama.cpp parameters in `application.yml`
2. **Load Testing**: Test with multiple concurrent requests
3. **Main App Integration**: Update PDF Studio to use microservice API
4. **Monitoring**: Set up Prometheus/Grafana for metrics
5. **Production**: Deploy with Docker Swarm or Kubernetes

---

**Ready? Start with Step 1!** 🚀
