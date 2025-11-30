package app.aiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * AI Service Microservice Application
 * 
 * Provides REST API endpoints for AI/LLM operations including:
 * - Document summarization
 * - Interactive chat
 * - Entity extraction
 * - Translation
 * - Insights generation
 * 
 * Features:
 * - llama.cpp integration for GGUF models
 * - Async processing support
 * - REST API endpoints
 * 
 * Note: Caching disabled to work without Redis/Docker
 */
@SpringBootApplication
@EnableAsync
public class AIServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AIServiceApplication.class, args);
    }
}
