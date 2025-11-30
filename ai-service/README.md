# AI Service - Microservice for PDF Studio

## Overview

The AI Service is a Spring Boot microservice that provides AI/LLM operations for the PDF Studio application. It uses:

- **LLaMA 3.2 3B** GGUF model for text generation
- **Redis** for response caching
- **RabbitMQ** for async processing
- **llama.cpp** for model inference

## Features

- ✅ REST API for AI operations
- ✅ Redis caching (100x faster for repeated requests)
- ✅ RabbitMQ for async processing
- ✅ Health check endpoints
- ✅ Docker support
- ✅ Horizontal scalability

## Quick Start

### Local Development

1. **Start dependencies:**
   ```bash
   docker run -d -p 6379:6379 --name redis redis:alpine
   docker run -d -p 5672:5672 -p 15672:15672 --name rabbitmq rabbitmq:management
   ```

2. **Configure model path** in `src/main/resources/application.yml`:
   ```yaml
   ai:
     model:
       path: ../models/Llama-3.2-3B-Instruct-Q6_K_L_2.gguf
   ```

3. **Run the service:**
   ```bash
   mvn spring-boot:run
   ```

4. **Test the service:**
   ```bash
   curl http://localhost:8081/api/ai/health
   ```

### Docker Deployment

```bash
# From project root
docker-compose up -d
```

This will start:
- Redis on port 6379
- RabbitMQ on ports 5672 (AMQP) and 15672 (Management UI)
- AI Service on port 8081

## API Endpoints

### Health Check
```bash
GET /api/ai/health
```

**Response:**
```json
{
  "status": "UP",
  "model": "../models/Llama-3.2-3B-Instruct-Q6_K_L_2.gguf",
  "modelLoaded": true
}
```

### Summarize Document
```bash
POST /api/ai/summarize
Content-Type: application/json

{
  "content": "Your document text here...",
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

### Chat with Context
```bash
POST /api/ai/chat
Content-Type: application/json

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

## Configuration

### application.yml

```yaml
server:
  port: 8081

spring:
  data:
    redis:
      host: localhost
      port: 6379
  rabbitmq:
    host: localhost
    port: 5672

ai:
  model:
    path: ../models/Llama-3.2-3B-Instruct-Q6_K_L_2.gguf
  cache:
    summary-ttl: 3600      # 1 hour
    chat-ttl: 1800         # 30 minutes
  generation:
    temperature: 0.7
    max-output-tokens: 512
```

## Caching Strategy

The service uses Redis to cache AI responses:

- **Summaries**: 1 hour TTL
- **Chat**: 30 minutes TTL
- **Entities**: 2 hours TTL
- **Translations**: 24 hours TTL

Cache keys are generated from: `hash(operation + content)`

## Performance

| Metric | Value |
|--------|-------|
| **Average Response Time** | 200-500ms (LLM) / 10ms (cached) |
| **Cache Hit Rate** | ~70% |
| **Throughput** | 10-20 req/sec (single instance) |
| **Scalability** | Horizontal (multiple instances) |

## Monitoring

- **Service Health**: http://localhost:8081/api/ai/health
- **RabbitMQ UI**: http://localhost:15672 (guest/guest)
- **Logs**: `logs/ai-service.log`

## Troubleshooting

### Service won't start

1. Check if Redis is running: `docker ps | grep redis`
2. Check if RabbitMQ is running: `docker ps | grep rabbitmq`
3. Verify model file exists: Check path in application.yml

### llama.cpp not found

1. Download from: https://github.com/ggerganov/llama.cpp
2. Build or get pre-built binaries
3. Place `llama-cli.exe` in configured path
4. Restart service

### Slow performance

1. Check cache hit ratio (should be >70%)
2. Verify Redis is connected
3. Consider enabling GPU: Set `-ngl 32` in AIModelService

## Development

### Build
```bash
mvn clean package
```

### Run Tests
```bash
mvn test
```

### Build Docker Image
```bash
docker build -t pdf-studio-ai-service .
```

## License

Same as PDF Studio main application.
