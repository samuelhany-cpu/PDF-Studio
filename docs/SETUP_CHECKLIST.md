# 📋 PDF Studio AI Microservice - Setup Checklist

Use this checklist to track your setup progress. Check off each item as you complete it.

---

## 🎯 Phase 1: Prerequisites

- [ ] **Java 17+ installed**
  - Check: `java -version`
  - Expected: Java version 17 or higher

- [ ] **Maven installed**
  - Check: `mvn -version`
  - Expected: Maven 3.6.3 or higher

- [x] **Docker Desktop installed** ✅
  - Check: `docker --version`
  - Status: Already installed by user

- [x] **GGUF Model downloaded** ✅
  - Location: `F:\PDF Studio\models\Llama-3.2-3B-Instruct-Q6_K_L_2.gguf`
  - Status: Already in place

- [x] **llama.cpp downloaded** ✅
  - File: `llama-b7067-bin-win-cuda-12.4-x64.zip`
  - Status: Downloaded (CUDA 12.4 version)

---

## 🔧 Phase 2: Installation

### Step 1: Extract llama.cpp
- [ ] Located download: `llama-b7067-bin-win-cuda-12.4-x64.zip`
- [ ] Extracted to: `F:\PDF Studio\llama.cpp\`
- [ ] Verified files present:
  - [ ] `llama-cli.exe`
  - [ ] `llama-server.exe`
  - [ ] `cudart64_12.dll`
  - [ ] `cublas64_12.dll`

### Step 2: Verify llama.cpp Installation
- [ ] Opened command prompt in `F:\PDF Studio\`
- [ ] Ran: `test-llama.bat`
- [ ] Saw: `[OK] llama-cli.exe found!`
- [ ] Saw: `[OK] GGUF model found!`
- [ ] Saw: `Everything is ready!`

### Step 3: Start Docker Infrastructure
- [ ] Docker Desktop is running
- [ ] Opened command prompt in `F:\PDF Studio\`
- [ ] Ran: `docker-compose up -d`
- [ ] Waited ~30 seconds for containers to start
- [ ] Ran: `docker ps`
- [ ] Saw 2 containers running (pdf-redis, pdf-rabbitmq)

### Step 4: Verify Docker Containers
- [ ] Ran: `docker exec pdf-redis redis-cli ping`
- [ ] Saw: `PONG`
- [ ] Checked Redis logs: `docker logs pdf-redis` (no errors)
- [ ] Checked RabbitMQ logs: `docker logs pdf-rabbitmq` (started successfully)

---

## ⚡ Phase 3: Enable Redis Caching

### File 1: application.yml
- [ ] Opened: `ai-service\src\main\resources\application.yml`
- [ ] Removed line: `enabled: false` (under spring.data.redis)
- [ ] Removed section: `spring.autoconfigure.exclude`
- [ ] Saved file

### File 2: AIServiceApplication.java
- [ ] Opened: `ai-service\src\main\java\com\pdfstudio\ai\AIServiceApplication.java`
- [ ] Added: `@EnableCaching` annotation before `public class`
- [ ] Saved file

### File 3: AIModelService.java
- [ ] Opened: `ai-service\src\main\java\com\pdfstudio\ai\service\AIModelService.java`
- [ ] Added import: `import org.springframework.cache.annotation.Cacheable;`
- [ ] Added annotation to `generateText()` method:
  ```java
  @Cacheable(value = "ai-generations", key = "#prompt.hashCode() + '_' + #maxTokens")
  ```
- [ ] Saved file

### File 4: AIOperationsService.java
- [ ] Opened: `ai-service\src\main\java\com\pdfstudio\ai\service\AIOperationsService.java`
- [ ] Added import: `import org.springframework.cache.annotation.Cacheable;`
- [ ] Added annotation to `summarize()`:
  ```java
  @Cacheable(value = "summaries", key = "#request.content.hashCode()")
  ```
- [ ] Added annotation to `chat()`:
  ```java
  @Cacheable(value = "chats", key = "#request.message + '_' + #request.context.orElse('no-context')")
  ```
- [ ] Saved file

---

## 🚀 Phase 4: Start AI Service

- [ ] Opened command prompt in `F:\PDF Studio\`
- [ ] Ran: `restart-ai-service.bat`
- [ ] Waited for service to start
- [ ] Saw: `Started AIServiceApplication in X.XXX seconds`
- [ ] Saw: `Model loaded successfully`
- [ ] Saw: `llama.cpp found at: F:/PDF Studio/llama.cpp/llama-cli.exe`
- [ ] No Redis connection errors in logs

---

## 🧪 Phase 5: Testing

### Health Check
- [ ] Opened PowerShell
- [ ] Ran: `Invoke-RestMethod http://localhost:8081/api/ai/health | ConvertTo-Json`
- [ ] Verified output:
  - [ ] `status: "UP"`
  - [ ] `modelLoaded: true`
  - [ ] `modelExists: true`
  - [ ] `llamaCppFound: true`
  - [ ] `llamaCppPath: "F:/PDF Studio/llama.cpp/llama-cli.exe"`

### Summarization Test (Not Cached)
- [ ] Ran: `.\test-ai-service.ps1` in PowerShell
- [ ] Test 1 (Health Check): ✓ PASSED
- [ ] Test 2 (Status Check): ✓ PASSED
- [ ] Test 3 (First Summarization): ✓ PASSED
  - [ ] Got real AI-generated summary (not stub message)
  - [ ] Processing time: 2-10 seconds
  - [ ] `cached: false`
  - [ ] `modelUsed: "Llama-3.2-3B-Instruct"`

### Caching Test (Should Be Cached)
- [ ] Test 4 (Second Summarization): ✓ PASSED
  - [ ] Got same summary
  - [ ] Processing time: < 100ms
  - [ ] `cached: true`
  - [ ] Speedup: 20x - 100x faster

### Chat Test
- [ ] Test 5 (Chat Request): ✓ PASSED
  - [ ] Got coherent AI response
  - [ ] Appropriate answer to question
  - [ ] Processing time recorded

---

## 📊 Phase 6: Performance Validation

### GPU Acceleration (Optional)
- [ ] Opened Task Manager → Performance → GPU
- [ ] Ran a summarization request
- [ ] Observed GPU utilization spike (if CUDA working)

**Note:** If no GPU spike, llama.cpp is using CPU (still works, just slower)

### Caching Efficiency
- [ ] Ran same request 3 times
- [ ] First request: ~3-10 seconds
- [ ] Second request: < 100ms (from cache)
- [ ] Third request: < 100ms (from cache)

### Concurrent Requests
- [ ] Ran `.\test-ai-service.ps1` in 3 PowerShell windows simultaneously
- [ ] All tests passed
- [ ] No crashes or errors
- [ ] Reasonable response times

---

## ✅ Final Verification

### All Systems Operational
- [ ] **llama.cpp**: Installed and working
- [ ] **Docker**: Redis + RabbitMQ running
- [ ] **AI Service**: Running on port 8081
- [ ] **Model**: LLaMA 3.2 3B loaded
- [ ] **Inference**: Real AI responses generated
- [ ] **Caching**: Redis caching functional
- [ ] **GPU**: CUDA acceleration active (optional)
- [ ] **Monitoring**: Health endpoint shows `UP`

### Documentation Review
- [ ] Read: `SETUP_GUIDE.md`
- [ ] Read: `docs/ENABLE_CACHING.md`
- [ ] Read: `docs/MICROSERVICES_ARCHITECTURE.md`
- [ ] Read: `ai-service/README.md`

---

## 🎉 Success Criteria

When ALL items above are checked, you have successfully:

✅ **Installed llama.cpp with CUDA support**  
✅ **Deployed Redis and RabbitMQ via Docker**  
✅ **Configured AI microservice with caching**  
✅ **Achieved real LLaMA 3.2 3B inference**  
✅ **Verified 20-100x speedup from caching**  
✅ **Created a production-ready microservices architecture**  

---

## 📝 Notes & Issues

Use this space to track any issues encountered:

**Issue 1:**
- Description: 
- Solution: 

**Issue 2:**
- Description: 
- Solution: 

---

## 🔄 Next Steps After Completion

Once all items are checked:

1. **Integrate with Main App**
   - Update `AIServiceImpl` to call REST API
   - Add HTTP client for microservice communication
   - Test end-to-end PDF summarization

2. **Performance Tuning**
   - Adjust llama.cpp parameters (`n_ctx`, `n_threads`)
   - Fine-tune Redis cache TTL
   - Optimize model quantization

3. **Production Deployment**
   - Create Docker image for AI service
   - Set up load balancing
   - Add monitoring (Prometheus/Grafana)
   - Implement circuit breakers

4. **Additional Features**
   - Document translation
   - Entity extraction
   - Semantic search
   - Multi-language support

---

**Date Started:** _______________  
**Date Completed:** _______________  
**Total Setup Time:** _______________ hours
