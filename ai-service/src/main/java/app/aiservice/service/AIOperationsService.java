package app.aiservice.service;

import app.aiservice.dto.ChatResponse;
import app.aiservice.dto.SummarizeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * AI Operations Service
 * Implements business logic for AI operations
 */
@Service
public class AIOperationsService {
    private static final Logger logger = LoggerFactory.getLogger(AIOperationsService.class);
    
    @Autowired
    private AIModelService aiModelService;
    
    @Value("${ai.generation.max-output-tokens:512}")
    private int maxOutputTokens;
    
    @PostConstruct
    public void init() {
        logger.info("Initializing AI Operations Service...");
        aiModelService.initialize();
    }
    
    /**
     * Get health status of AI service
     */
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", aiModelService.isModelLoaded() ? "UP" : "DEGRADED");
        status.put("model", aiModelService.getModelPath());
        status.put("modelLoaded", aiModelService.isModelLoaded());
        return status;
    }
    
    /**
     * Generate document summary
     * Note: Caching disabled to work without Redis
     */
    public SummarizeResponse summarize(String content, String filename, int maxLength) {
        logger.info("Generating summary for: {} ({} chars)", filename, content.length());
        
        // Truncate content if too long (keep first part)
        String truncatedContent = content.length() > 4000 
            ? content.substring(0, 4000) + "..." 
            : content;
        
        String prompt = String.format(
            "Summarize the following document in %d words or less:\n\n%s\n\nSummary:",
            maxLength / 5, // rough words estimate
            truncatedContent
        );
        
        String summary = aiModelService.generateText(prompt, maxLength);
        
        return SummarizeResponse.builder()
            .summary(summary)
            .cached(false)
            .modelUsed(aiModelService.getModelPath())
            .build();
    }
    
    /**
     * Interactive chat with context
     * Note: Caching disabled to work without Redis
     */
    public ChatResponse chat(String message, String context, String conversationId) {
        logger.info("Processing chat message: {}", message);
        
        // Truncate context if too long
        String truncatedContext = context != null && context.length() > 3000
            ? context.substring(0, 3000) + "..."
            : context;
        
        String prompt;
        if (truncatedContext != null && !truncatedContext.isEmpty()) {
            prompt = String.format(
                "Context:\n%s\n\nUser question: %s\n\nAssistant:",
                truncatedContext,
                message
            );
        } else {
            prompt = String.format("User: %s\n\nAssistant:", message);
        }
        
        String response = aiModelService.generateText(prompt, maxOutputTokens);
        
        return ChatResponse.builder()
            .response(response)
            .cached(false)
            .build();
    }
}
