# 🔄 Re-enabling Redis Caching

This guide shows you exactly what to change to re-enable Redis caching after Docker is running.

---

## Prerequisites ✅

Before proceeding, ensure:
- ✅ Docker Desktop is running
- ✅ Containers started: `docker-compose up -d`
- ✅ Redis is healthy: `docker exec pdf-redis redis-cli ping` returns `PONG`

---

## Changes Required

### 1. Update `application.yml`

**File:** `ai-service/src/main/resources/application.yml`

**Remove these lines:**

```yaml
spring:
  data:
    redis:
      enabled: false  # ← DELETE THIS
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration  # ← DELETE THIS
```

**After removal, the Redis section should look like:**

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2000ms
  cache:
    redis:
      time-to-live: 3600000
      cache-null-values: false
```

---

### 2. Update `AIServiceApplication.java`

**File:** `ai-service/src/main/java/com/pdfstudio/ai/AIServiceApplication.java`

**Add back the `@EnableCaching` annotation:**

```java
package com.pdfstudio.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching  // ← ADD THIS BACK
public class AIServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AIServiceApplication.class, args);
    }
}
```

---

### 3. Update `AIModelService.java`

**File:** `ai-service/src/main/java/com/pdfstudio/ai/service/AIModelService.java`

**Add the import:**
```java
import org.springframework.cache.annotation.Cacheable;
```

**Add back the `@Cacheable` annotation to the `generateText()` method:**

```java
@Cacheable(value = "ai-generations", key = "#prompt.hashCode() + '_' + #maxTokens")
public String generateText(String prompt, int maxTokens) {
    if (!modelLoaded) {
        return "AI model not yet loaded. Please wait for initialization.";
    }
    
    try {
        return generateViaLlamaCpp(prompt, maxTokens);
    } catch (Exception e) {
        logger.error("Error generating text", e);
        return "Error generating response: " + e.getMessage();
    }
}
```

---

### 4. Update `AIOperationsService.java`

**File:** `ai-service/src/main/java/com/pdfstudio/ai/service/AIOperationsService.java`

**Add the import:**
```java
import org.springframework.cache.annotation.Cacheable;
```

**Add back `@Cacheable` to the `summarize()` method:**

```java
@Cacheable(value = "summaries", key = "#request.content.hashCode()")
public SummarizeResponse summarize(SummarizeRequest request) {
    long startTime = System.currentTimeMillis();
    
    // ... rest of method
}
```

**Add back `@Cacheable` to the `chat()` method:**

```java
@Cacheable(value = "chats", key = "#request.message + '_' + #request.context.orElse('no-context')")
public ChatResponse chat(ChatRequest request) {
    long startTime = System.currentTimeMillis();
    
    // ... rest of method
}
```

---

## Quick Apply Script (Copy-Paste)

For **PowerShell**, you can apply all changes at once:

```powershell
# Navigate to project directory
cd "F:\PDF Studio"

# Backup current files
Copy-Item "ai-service\src\main\resources\application.yml" "ai-service\src\main\resources\application.yml.backup-nocache"

# Edit application.yml (manual step - remove the lines mentioned above)

# Then rebuild and restart
cd ai-service
mvn clean compile
cd ..
restart-ai-service.bat
```

---

## Verification

After making changes and restarting the service:

### 1. Check Health Endpoint
```powershell
Invoke-RestMethod http://localhost:8081/api/ai/health | ConvertTo-Json
```

Expected:
```json
{
  "status": "UP",
  "modelLoaded": true,
  "redisConnected": true  // ← Should be true now
}
```

### 2. Test Caching

**First request (not cached):**
```powershell
$body = @{
    content = "Test document for caching"
    filename = "test.pdf"
    maxLength = 50
} | ConvertTo-Json

Invoke-RestMethod -Uri http://localhost:8081/api/ai/summarize `
    -Method POST -Body $body -ContentType "application/json"
```

Note the `processingTimeMs` (e.g., 3000ms) and `cached: false`.

**Second request (should be cached):**
```powershell
# Run the EXACT same request again
Invoke-RestMethod -Uri http://localhost:8081/api/ai/summarize `
    -Method POST -Body $body -ContentType "application/json"
```

Now you should see:
- `processingTimeMs`: ~50ms (much faster!)
- `cached: true`

---

## Check Redis Cache Contents

```cmd
docker exec -it pdf-redis redis-cli

# In Redis CLI:
KEYS *
GET summaries::123456789  # (use actual key from KEYS output)
```

---

## Rollback (If Needed)

If caching causes issues, restore the backup:

```powershell
Copy-Item "ai-service\src\main\resources\application.yml.backup-nocache" "ai-service\src\main\resources\application.yml" -Force
restart-ai-service.bat
```

---

## Done! ✅

Your AI microservice now has:
- ✅ Redis caching enabled
- ✅ Faster response times for repeated requests
- ✅ Reduced load on llama.cpp
- ✅ Production-ready caching layer
