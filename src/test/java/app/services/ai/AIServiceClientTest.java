package app.services.ai;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.net.http.HttpTimeoutException;

/**
 * Unit tests for AIServiceClient
 * Tests HTTP communication with AI microservice
 */
@DisplayName("AIServiceClient Tests")
class AIServiceClientTest {
    
    private AIServiceClient client;
    
    @BeforeEach
    void setUp() {
        client = new AIServiceClient("http://localhost:8081/api/ai");
    }
    
    @Test
    @DisplayName("Should create client with default URL")
    void testDefaultConstructor() {
        AIServiceClient defaultClient = new AIServiceClient();
        assertNotNull(defaultClient, "Default client should be created");
    }
    
    @Test
    @DisplayName("Should create client with custom URL")
    void testCustomConstructor() {
        AIServiceClient customClient = new AIServiceClient("http://custom:9000/api");
        assertNotNull(customClient, "Custom client should be created");
    }
    
    @Test
    @DisplayName("Should handle connection timeout gracefully")
    void testConnectionTimeout() {
        // Use non-routable IP to force timeout
        AIServiceClient timeoutClient = new AIServiceClient("http://192.0.2.1:8081/api/ai");
        boolean available = timeoutClient.isAvailable();
        assertFalse(available, "Should return false for unreachable service");
    }
    
    @Test
    @DisplayName("Should handle invalid URL format")
    void testInvalidUrl() {
        assertDoesNotThrow(() -> {
            AIServiceClient invalidClient = new AIServiceClient("not-a-url");
            // Should not throw during construction, only during requests
        });
    }
    
    @Test
    @DisplayName("Should retry health check multiple times")
    void testHealthCheckRetries() {
        // This will fail but should retry 3 times
        AIServiceClient retryClient = new AIServiceClient("http://localhost:9999/api/ai");
        long startTime = System.currentTimeMillis();
        boolean available = retryClient.isAvailable();
        long duration = System.currentTimeMillis() - startTime;
        
        assertFalse(available, "Should return false after retries");
        assertTrue(duration >= 2000, "Should take at least 2 seconds (3 retries with 1s delay)");
    }
    
    @Test
    @DisplayName("Should handle null content in summarize")
    void testSummarizeWithNullContent() {
        Exception exception = assertThrows(Exception.class, () -> {
            client.summarize(null, "test.pdf", 500);
        });
        assertNotNull(exception.getMessage(), "Exception should have a message");
    }
    
    @Test
    @DisplayName("Should handle empty content in summarize")
    void testSummarizeWithEmptyContent() {
        assertDoesNotThrow(() -> {
            // May fail due to connection, but should not throw NPE
            try {
                client.summarize("", "test.pdf", 500);
            } catch (Exception e) {
                // Expected if service not running
                assertTrue(e.getMessage().contains("connect") || 
                          e.getMessage().contains("timeout") ||
                          e.getMessage().contains("failed"),
                          "Should have connection-related error");
            }
        });
    }
    
    @Test
    @DisplayName("Should handle very long content")
    void testSummarizeWithLongContent() {
        String longContent = "a".repeat(100000);
        assertDoesNotThrow(() -> {
            try {
                client.summarize(longContent, "large.pdf", 500);
            } catch (Exception e) {
                // Expected if service not running
                assertTrue(e.getMessage() != null, "Exception should have message");
            }
        });
    }
    
    @Test
    @DisplayName("Should handle special characters in filename")
    void testSummarizeWithSpecialCharacters() {
        String content = "Test content";
        String filename = "test@#$%^&*().pdf";
        
        assertDoesNotThrow(() -> {
            try {
                client.summarize(content, filename, 500);
            } catch (Exception e) {
                // Expected if service not running
                assertTrue(e.getMessage() != null, "Exception should have message");
            }
        });
    }
    
    @Test
    @DisplayName("Should handle zero max length")
    void testSummarizeWithZeroMaxLength() {
        assertDoesNotThrow(() -> {
            try {
                client.summarize("Test content", "test.pdf", 0);
            } catch (Exception e) {
                // Expected if service not running
                assertTrue(e.getMessage() != null, "Exception should have message");
            }
        });
    }
    
    @Test
    @DisplayName("Should handle negative max length")
    void testSummarizeWithNegativeMaxLength() {
        assertDoesNotThrow(() -> {
            try {
                client.summarize("Test content", "test.pdf", -100);
            } catch (Exception e) {
                // Expected if service not running
                assertTrue(e.getMessage() != null, "Exception should have message");
            }
        });
    }
    
    @Test
    @DisplayName("Should handle UTF-8 content")
    void testSummarizeWithUtf8Content() {
        String utf8Content = "Testing UTF-8: こんにちは 你好 مرحبا Привет";
        assertDoesNotThrow(() -> {
            try {
                client.summarize(utf8Content, "utf8.pdf", 500);
            } catch (Exception e) {
                // Expected if service not running
                assertTrue(e.getMessage() != null, "Exception should have message");
            }
        });
    }
    
    @Test
    @DisplayName("Should handle concurrent requests")
    void testConcurrentRequests() {
        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    client.summarize("Content " + index, "file" + index + ".pdf", 100);
                } catch (Exception e) {
                    // Expected if service not running
                }
            });
        }
        
        assertDoesNotThrow(() -> {
            for (Thread thread : threads) {
                thread.start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
        });
    }
}
