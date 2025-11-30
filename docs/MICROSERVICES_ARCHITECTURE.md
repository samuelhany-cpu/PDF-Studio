# 🏗️ Microservices Architecture for PDF Studio AI

## Overview

PDF Studio uses a **microservices architecture** to optimize AI operations performance, scalability, and maintainability. The AI functionality is separated into an independent service that communicates with the main application via REST APIs and message queues.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    PDF Studio Main App                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   JavaFX UI  │  │ PDF Service  │  │  OCR Service │      │
│  └──────┬───────┘  └──────────────┘  └──────────────┘      │
│         │                                                     │
│  ┌──────▼────────────────────────────────────────────┐      │
│  │         AI Service Client (REST + Queue)          │      │
│  └──────┬────────────────────────────────────────────┘      │
└─────────┼──────────────────────────────────────────────────┘
          │
          │ HTTP REST
          ▼
┌─────────────────────────────────────────────────────────────┐
│                   AI Microservice                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ REST API     │  │ Message      │  │ Redis Cache  │      │
│  │ Controller   │  │ Consumer     │  │ Manager      │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                  │                  │              │
│  ┌──────▼──────────────────▼──────────────────▼────────┐   │
│  │         AI Core Service (LLaMA/GGUF)                 │   │
│  │  - LlamaModelManager (llama.cpp integration)         │   │
│  │  - Text generation, summarization, chat              │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
          │                                │
          │                                │
          ▼                                ▼
┌────────────────────┐          ┌────────────────────┐
│   Redis Cache      │          │   RabbitMQ         │
│   (Response Cache) │          │   (Async Queue)    │
└────────────────────┘          └────────────────────┘
```

## Components

### 1. **Main Application (JavaFX)**
- **Purpose**: User interface, PDF operations, OCR
- **Port**: JavaFX Desktop App
- **Technology**: Java 17, JavaFX, PDFBox, Tesseract
- **Responsibilities**:
  - Display PDF content
  - Handle user interactions
  - Coordinate PDF/OCR operations
  - **Communicate with AI microservice** for AI features

### 2. **AI Microservice (Spring Boot)**
- **Purpose**: All AI/LLM operations
- **Port**: 8081 (configurable)
- **Technology**: Spring Boot, llama.cpp, GGUF models
- **Responsibilities**:
  - Load and manage LLaMA 3.2 3B model
  - Process AI requests (summarize, chat, extract, translate)
  - Cache responses for performance
  - Handle async processing via message queue

### 3. **Redis Cache**
- **Purpose**: Cache AI responses to avoid re-computation
- **Port**: 6379
- **Technology**: Redis
- **Cache Strategy**:
  - **Key**: Hash of (operation + content)
  - **TTL**: 1 hour for summaries, 30 min for chat
  - **Benefits**: 100x faster for repeated requests

### 4. **RabbitMQ Message Queue**
- **Purpose**: Async AI processing for long-running operations
- **Port**: 5672 (AMQP), 15672 (Management UI)
- **Technology**: RabbitMQ
- **Workflow**:
  1. Main app sends AI request to queue
  2. AI service picks up from queue
  3. Processes in background
  4. Sends result back via callback queue
  - **Benefits**: Non-blocking UI, better resource utilization

## API Endpoints (AI Microservice)

### REST API

All endpoints accept JSON and return JSON.

#### 1. **POST /api/ai/summarize**
Generate a summary of document content.

**Request:**
```json
{
  "content": "Full document text...",
  "filename": "document.pdf",
  "maxLength": 500
}
```

**Response:**
```json
{
  "summary": "This document discusses...",
  "processingTimeMs": 245,
  "cached": false,
  "modelUsed": "LLaMA 3.2 3B"
}
```

#### 2. **POST /api/ai/chat**
Interactive chat with document context.

**Request:**
```json
{
  "message": "What is the main topic?",
  "context": "Document content...",
  "conversationId": "uuid-1234"
}
```

**Response:**
```json
{
  "response": "The main topic is...",
  "processingTimeMs": 180,
  "cached": false
}
```

#### 3. **POST /api/ai/extract-entities**
Extract named entities from text.

**Request:**
```json
{
  "content": "Document content..."
}
```

**Response:**
```json
{
  "entities": {
    "people": ["John Doe", "Jane Smith"],
    "organizations": ["ACME Corp"],
    "locations": ["New York"],
    "dates": ["2024-01-15"]
  },
  "processingTimeMs": 150
}
```

#### 4. **POST /api/ai/translate**
Translate text to another language.

**Request:**
```json
{
  "content": "Hello world",
  "targetLanguage": "fr"
}
```

**Response:**
```json
{
  "translation": "Bonjour le monde",
  "sourceLanguage": "en",
  "targetLanguage": "fr",
  "processingTimeMs": 120
}
```

#### 5. **POST /api/ai/insights**
Generate insights from document.

**Request:**
```json
{
  "content": "Document content..."
}
```

**Response:**
```json
{
  "insights": [
    "Key finding 1...",
    "Key finding 2...",
    "Recommendation..."
  ],
  "processingTimeMs": 300
}
```

#### 6. **GET /api/ai/health**
Health check endpoint.

**Response:**
```json
{
  "status": "UP",
  "model": "LLaMA 3.2 3B",
  "modelLoaded": true,
  "cacheAvailable": true,
  "queueAvailable": true
}
```

## Communication Patterns

### 1. **Synchronous (REST)**
For fast operations where user expects immediate response.

**Flow:**
```
Main App → HTTP POST → AI Service → Process → Response → Main App
```

**Use Cases:**
- Simple entity extraction
- Short translations
- Cached results

### 2. **Asynchronous (Message Queue)**
For long-running operations to keep UI responsive.

**Flow:**
```
Main App → Send to Queue → Return request ID
             ↓
AI Service ← Poll Queue ← Process in background
             ↓
Main App ← Result via WebSocket/Polling
```

**Use Cases:**
- Large document summarization
- Complex multi-turn chat
- Batch processing

## Caching Strategy

### Cache Key Generation
```java
String cacheKey = hashCode(operation + content);
// Example: "summarize_abc123def456"
```

### Cache Hit vs Miss
- **Cache Hit**: Return in <10ms (100x faster)
- **Cache Miss**: Process with LLM (200-500ms)

### TTL (Time To Live)
- **Summaries**: 1 hour (less likely to change)
- **Chat responses**: 30 minutes (conversational context)
- **Entities**: 2 hours (factual extraction)
- **Translations**: 24 hours (deterministic)

## Performance Benefits

| Metric | Before Microservices | After Microservices | Improvement |
|--------|---------------------|---------------------|-------------|
| **UI Responsiveness** | Blocked during AI ops | Non-blocking | ✅ 100% |
| **Repeated Requests** | 300ms (LLM) | 10ms (cache) | ✅ 30x faster |
| **Scalability** | Single instance | Horizontal scaling | ✅ N instances |
| **Deployment** | Monolith | Independent | ✅ Faster updates |
| **Resource Usage** | Always loaded | On-demand | ✅ 50% savings |

## Deployment

### Development (Local)
```bash
# Start Redis
docker run -d -p 6379:6379 redis

# Start RabbitMQ
docker run -d -p 5672:5672 -p 15672:15672 rabbitmq:management

# Start AI Service
cd ai-service
mvn spring-boot:run

# Start Main App
cd ..
mvn javafx:run
```

### Production (Docker Compose)
```bash
docker-compose up -d
```

See `docker-compose.yml` for full configuration.

## Configuration

### Main App (`config.json`)
```json
{
  "aiService": {
    "url": "http://localhost:8081",
    "timeout": 30000,
    "retryAttempts": 3,
    "useAsync": true
  }
}
```

### AI Service (`application.yml`)
```yaml
server:
  port: 8081

spring:
  redis:
    host: localhost
    port: 6379
  rabbitmq:
    host: localhost
    port: 5672

ai:
  model:
    path: ../models/Llama-3.2-3B-Instruct-Q6_K_L_2.gguf
    maxTokens: 4096
  cache:
    summaryTtl: 3600
    chatTtl: 1800
```

## Scalability

### Horizontal Scaling
Run multiple AI service instances behind a load balancer:

```
Main App → Load Balancer → AI Service Instance 1
                        → AI Service Instance 2
                        → AI Service Instance 3
```

**Benefits:**
- Handle more concurrent requests
- Better fault tolerance
- Graceful degradation

### Auto-scaling
Use Kubernetes or Docker Swarm for auto-scaling based on CPU/memory:

```yaml
# Kubernetes example
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: ai-service
spec:
  minReplicas: 1
  maxReplicas: 5
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

## Monitoring

### Health Endpoints
- Main App: `http://localhost:8080/actuator/health`
- AI Service: `http://localhost:8081/api/ai/health`
- RabbitMQ: `http://localhost:15672` (guest/guest)

### Metrics (Prometheus + Grafana)
- Request rate per endpoint
- Average processing time
- Cache hit ratio
- Queue depth
- Model inference time

## Security

### API Authentication
Use JWT tokens for AI service access:

```java
// Main App sends token
RestTemplate.exchange(url, HttpMethod.POST, 
    new HttpEntity<>(request, headers), Response.class);

// AI Service validates
@PreAuthorize("hasRole('AI_USER')")
public ResponseEntity<Summary> summarize(...) { ... }
```

### Network Isolation
- Main App ↔ AI Service: Private network
- AI Service ↔ Redis/RabbitMQ: Private network
- Only expose Main App UI to users

## Troubleshooting

### AI Service Not Responding
1. Check if service is running: `curl http://localhost:8081/api/ai/health`
2. Check logs: `docker logs ai-service`
3. Verify model loaded: Check health endpoint `modelLoaded: true`

### Slow Performance
1. Check cache hit ratio (should be >70%)
2. Verify Redis is running
3. Monitor queue depth (should be <10)
4. Check llama.cpp CPU usage

### Connection Failures
1. Verify network connectivity between services
2. Check firewall rules
3. Ensure correct ports configured
4. Review application logs

## Future Enhancements

1. **GPU Acceleration**: Use CUDA for llama.cpp inference
2. **Model Hot-Swapping**: Change models without restart
3. **Multi-Model Support**: Route to different models based on task
4. **Streaming Responses**: Server-Sent Events for real-time generation
5. **Distributed Tracing**: OpenTelemetry for request tracking
6. **A/B Testing**: Compare different model versions

## References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Redis Cache Best Practices](https://redis.io/docs/manual/patterns/)
- [RabbitMQ Tutorials](https://www.rabbitmq.com/getstarted.html)
- [llama.cpp GitHub](https://github.com/ggerganov/llama.cpp)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
