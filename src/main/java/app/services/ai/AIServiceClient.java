package app.services.ai;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * HTTP client for communicating with the AI microservice
 */
public class AIServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(AIServiceClient.class);
    private static final String DEFAULT_AI_SERVICE_URL = "http://localhost:8081/api/ai";
    
    private final HttpClient httpClient;
    private final Gson gson;
    private final String serviceUrl;
    
    public AIServiceClient() {
        this(DEFAULT_AI_SERVICE_URL);
    }
    
    public AIServiceClient(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }
    
    /**
     * Generate a summary via the AI microservice
     */
    public SummaryResponse summarize(String content, String filename, int maxLength) throws Exception {
        logger.debug("Calling AI microservice for summary: {}", filename);
        
        try {
            // Create request body
            SummarizeRequest request = new SummarizeRequest(content, filename, maxLength);
            String requestBody = gson.toJson(request);
            
            // Build HTTP request
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(serviceUrl + "/summarize"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            // Send request
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                SummaryResponse summaryResponse = gson.fromJson(response.body(), SummaryResponse.class);
                logger.info("Summary received from microservice in {}ms (cached: {})", 
                        summaryResponse.getProcessingTimeMs(), summaryResponse.isCached());
                return summaryResponse;
            } else {
                String errorMsg = "AI microservice returned error: " + response.statusCode() + " - " + response.body();
                logger.error("Microservice error: {}", errorMsg);
                throw new Exception(errorMsg);
            }
        } catch (java.net.http.HttpTimeoutException e) {
            logger.error("Timeout calling AI microservice for {}: {}", filename, e.getMessage());
            throw new Exception("AI microservice timeout: " + e.getMessage(), e);
        } catch (java.net.ConnectException e) {
            logger.error("Connection failed to AI microservice: {}", e.getMessage());
            throw new Exception("Cannot connect to AI microservice at " + serviceUrl + ": " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error calling AI microservice: {}", e.getMessage(), e);
            throw new Exception("AI microservice call failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if the AI microservice is available
     * Retries multiple times with delays to handle startup timing
     */
    public boolean isAvailable() {
        int maxRetries = 3;
        int retryDelayMs = 1000; // 1 second between retries
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(serviceUrl + "/health"))
                        .timeout(Duration.ofSeconds(3))
                        .GET()
                        .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    logger.info("AI microservice is available at {}", serviceUrl);
                    return true;
                }
                
                logger.debug("Health check attempt {}/{} returned status: {}", attempt, maxRetries, response.statusCode());
                
            } catch (java.net.ConnectException e) {
                logger.debug("Connection attempt {}/{} failed: {}", attempt, maxRetries, e.getMessage());
            } catch (Exception e) {
                logger.debug("Health check attempt {}/{} failed: {}", attempt, maxRetries, e.getMessage());
            }
            
            // Wait before retry (except on last attempt)
            if (attempt < maxRetries) {
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        logger.warn("AI microservice not available after {} attempts at {}", maxRetries, serviceUrl);
        return false;
    }
    
    /**
     * Request DTO for summarization
     */
    private static class SummarizeRequest {
        public String content;
        public String filename;
        public int maxLength;
        
        public SummarizeRequest(String content, String filename, int maxLength) {
            this.content = content;
            this.filename = filename;
            this.maxLength = maxLength;
        }
    }
    
    /**
     * Response DTO from microservice
     */
    public static class SummaryResponse {
        private String summary;
        private long processingTimeMs;
        private boolean cached;
        private String modelUsed;
        
        // Getters and setters
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        
        public long getProcessingTimeMs() { return processingTimeMs; }
        public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
        
        public boolean isCached() { return cached; }
        public void setCached(boolean cached) { this.cached = cached; }
        
        public String getModelUsed() { return modelUsed; }
        public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }
    }
}
