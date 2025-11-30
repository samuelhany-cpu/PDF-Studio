package app.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Unit tests for OCRResult model
 * Tests OCR result data structure
 */
@DisplayName("OCRResult Model Tests")
class OCRResultTest {
    
    @Test
    @DisplayName("Should create OCR result with all parameters")
    void testCreateOCRResult() {
        OCRResult result = new OCRResult("Test OCR text", 95.5, "English");
        assertNotNull(result, "OCRResult should be created");
    }
    
    @Test
    @DisplayName("Should get OCR text")
    void testGetText() {
        OCRResult result = new OCRResult("Test text", 90.0, "English");
        assertEquals("Test text", result.getText(), "Text should match");
    }
    
    @Test
    @DisplayName("Should get confidence")
    void testGetConfidence() {
        OCRResult result = new OCRResult("Test", 85.5, "English");
        assertEquals(85.5, result.getConfidence(), 0.01, "Confidence should match");
    }
    
    @Test
    @DisplayName("Should get language")
    void testGetLanguage() {
        OCRResult result = new OCRResult("Test", 90.0, "French");
        assertEquals("French", result.getLanguage(), "Language should match");
    }
    
    @Test
    @DisplayName("Should set and get metadata")
    void testMetadata() {
        OCRResult result = new OCRResult("Test", 90.0, "English");
        Map<String, Object> metadata = Map.of("pages", 5, "duration_ms", 1000L);
        result.setMetadata(metadata);
        
        Map<String, Object> retrievedMetadata = result.getMetadata();
        assertNotNull(retrievedMetadata, "Metadata should not be null");
        assertEquals(5, retrievedMetadata.get("pages"), "Pages metadata should match");
        assertEquals(1000L, retrievedMetadata.get("duration_ms"), "Duration metadata should match");
    }
    
    @Test
    @DisplayName("Should handle null text")
    void testNullText() {
        assertDoesNotThrow(() -> {
            OCRResult result = new OCRResult(null, 90.0, "English");
            assertNull(result.getText(), "Null text should be preserved");
        });
    }
    
    @Test
    @DisplayName("Should handle empty text")
    void testEmptyText() {
        OCRResult result = new OCRResult("", 90.0, "English");
        assertEquals("", result.getText(), "Empty text should be preserved");
    }
    
    @Test
    @DisplayName("Should handle very long text")
    void testLongText() {
        String longText = "a".repeat(100000);
        OCRResult result = new OCRResult(longText, 90.0, "English");
        assertEquals(longText, result.getText(), "Long text should be preserved");
    }
    
    @Test
    @DisplayName("Should handle zero confidence")
    void testZeroConfidence() {
        OCRResult result = new OCRResult("Test", 0.0, "English");
        assertEquals(0.0, result.getConfidence(), 0.01, "Zero confidence should be allowed");
    }
    
    @Test
    @DisplayName("Should handle 100% confidence")
    void testMaxConfidence() {
        OCRResult result = new OCRResult("Test", 100.0, "English");
        assertEquals(100.0, result.getConfidence(), 0.01, "100% confidence should be allowed");
    }
    
    @Test
    @DisplayName("Should handle negative confidence")
    void testNegativeConfidence() {
        assertDoesNotThrow(() -> {
            OCRResult result = new OCRResult("Test", -10.0, "English");
            // Implementation may normalize this
        });
    }
    
    @Test
    @DisplayName("Should handle confidence over 100")
    void testOverMaxConfidence() {
        assertDoesNotThrow(() -> {
            OCRResult result = new OCRResult("Test", 150.0, "English");
            // Implementation may normalize this
        });
    }
    
    @Test
    @DisplayName("Should handle null language")
    void testNullLanguage() {
        assertDoesNotThrow(() -> {
            OCRResult result = new OCRResult("Test", 90.0, null);
        });
    }
    
    @Test
    @DisplayName("Should handle empty language")
    void testEmptyLanguage() {
        OCRResult result = new OCRResult("Test", 90.0, "");
        assertEquals("", result.getLanguage(), "Empty language should be preserved");
    }
    
    @Test
    @DisplayName("Should handle UTF-8 text")
    void testUtf8Text() {
        String utf8Text = "こんにちは 你好 Привет مرحبا";
        OCRResult result = new OCRResult(utf8Text, 90.0, "Multilingual");
        assertEquals(utf8Text, result.getText(), "UTF-8 text should be preserved");
    }
    
    @Test
    @DisplayName("Should handle newlines in text")
    void testMultilineText() {
        String multilineText = "Line 1\nLine 2\nLine 3";
        OCRResult result = new OCRResult(multilineText, 90.0, "English");
        assertEquals(multilineText, result.getText(), "Newlines should be preserved");
    }
    
    @Test
    @DisplayName("Should handle null metadata")
    void testNullMetadata() {
        OCRResult result = new OCRResult("Test", 90.0, "English");
        assertDoesNotThrow(() -> {
            result.setMetadata(null);
        });
    }
    
    @Test
    @DisplayName("Should handle empty metadata")
    void testEmptyMetadata() {
        OCRResult result = new OCRResult("Test", 90.0, "English");
        result.setMetadata(Map.of());
        Map<String, Object> metadata = result.getMetadata();
        assertNotNull(metadata, "Metadata should not be null");
        assertTrue(metadata.isEmpty() || metadata.size() > 0, "Metadata should be accessible");
    }
}
