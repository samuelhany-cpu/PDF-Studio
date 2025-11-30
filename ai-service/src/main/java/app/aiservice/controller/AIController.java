package app.aiservice.controller;

import app.aiservice.dto.ChatRequest;
import app.aiservice.dto.ChatResponse;
import app.aiservice.dto.SummarizeRequest;
import app.aiservice.dto.SummarizeResponse;
import app.aiservice.service.AIOperationsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for AI operations
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {
    private static final Logger logger = LoggerFactory.getLogger(AIController.class);
    
    @Autowired
    private AIOperationsService aiOperationsService;
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(aiOperationsService.getHealthStatus());
    }
    
    /**
     * Summarize document content
     */
    @PostMapping("/summarize")
    public ResponseEntity<SummarizeResponse> summarize(@Valid @RequestBody SummarizeRequest request) {
        logger.info("Summarize request for file: {}", request.getFilename());
        long startTime = System.currentTimeMillis();
        
        try {
            SummarizeResponse response = aiOperationsService.summarize(
                request.getContent(),
                request.getFilename(),
                request.getMaxLength()
            );
            
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            logger.info("Summary generated in {}ms", response.getProcessingTimeMs());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to generate summary: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(SummarizeResponse.builder()
                    .summary("Error: " + e.getMessage())
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .cached(false)
                    .build());
        }
    }
    
    /**
     * Interactive chat with context
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        logger.info("Chat request: {}", request.getMessage());
        long startTime = System.currentTimeMillis();
        
        try {
            ChatResponse response = aiOperationsService.chat(
                request.getMessage(),
                request.getContext(),
                request.getConversationId()
            );
            
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            logger.info("Chat response generated in {}ms", response.getProcessingTimeMs());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to generate chat response: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(ChatResponse.builder()
                    .response("Error: " + e.getMessage())
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .cached(false)
                    .build());
        }
    }
}
