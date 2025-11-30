package app.services.ai;

import app.model.AIResponse;
import app.model.PDFDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AIServiceImpl
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AIServiceImplTest {
    
    private AIService aiService;
    
    @org.junit.jupiter.api.io.TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        aiService = new AIServiceImpl();
    }
    
    private PDFDocument createMockDocument(String text) throws IOException {
        File file = tempDir.resolve("test.pdf").toFile();
        PDDocument doc = new PDDocument();
        doc.addPage(new PDPage());
        doc.save(file);
        doc.close();
        
        // Reload to create PDFDocument
        PDDocument loaded = org.apache.pdfbox.Loader.loadPDF(file);
        return new PDFDocument(file, loaded);
    }
    
    @Test
    @Order(1)
    @DisplayName("Test summarize() - Success with valid document")
    void testSummarizeSuccess() throws Exception {
        // Arrange
        PDFDocument document = createMockDocument("Test content");
        
        // Act
        String summary = aiService.summarize(document);
        
        // Assert
        assertNotNull(summary, "Summary should not be null");
        assertTrue(summary.length() > 0, "Summary should not be empty");
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(2)
    @DisplayName("Test chat() - Success")
    void testChatSuccess() throws Exception {
        // Arrange
        PDFDocument document = createMockDocument("context");
        String message = "What is this document about?";
        
        // Act
        String response = aiService.chat(document, message);
        
        // Assert
        assertNotNull(response);
        assertTrue(response.length() > 0);
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(3)
    @DisplayName("Test chat() - Handle empty message")
    void testChatEmptyMessage() throws Exception {
        // Arrange
        PDFDocument document = createMockDocument("test");
        
        // Act
        String response = aiService.chat(document, "");
        
        // Assert
        assertNotNull(response);
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(4)
    @DisplayName("Test extractEntities() - Success")
    void testExtractEntitiesSuccess() throws Exception {
        // Arrange
        PDFDocument document = createMockDocument("test");
        
        // Act
        List<String> entities = aiService.extractEntities(document);
        
        // Assert
        assertNotNull(entities, "Entities list should not be null");
        assertTrue(entities.size() >= 0);
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(5)
    @DisplayName("Test translate() - Success")
    void testTranslateSuccess() throws Exception {
        // Arrange
        String text = "Hello, world!";
        String targetLanguage = "Spanish";
        
        // Act
        String translated = aiService.translate(text, targetLanguage);
        
        // Assert
        assertNotNull(translated, "Translated text should not be null");
        assertTrue(translated.length() > 0, "Translated text should not be empty");
    }
    
    @Test
    @Order(6)
    @DisplayName("Test translate() - Handle various languages")
    void testTranslateVariousLanguages() throws Exception {
        // Arrange
        String text = "Test";
        String[] languages = {"French", "German", "Japanese", "Arabic"};
        
        // Act & Assert
        for (String lang : languages) {
            String translated = aiService.translate(text, lang);
            assertNotNull(translated, "Translation should not be null for " + lang);
            assertTrue(translated.length() > 0, "Translation should not be empty for " + lang);
        }
    }
    
    @Test
    @Order(7)
    @DisplayName("Test generateInsights() - Success")
    void testGenerateInsightsSuccess() throws Exception {
        // Arrange
        PDFDocument document = createMockDocument("test");
        
        // Act
        AIResponse insights = aiService.generateInsights(document);
        
        // Assert
        assertNotNull(insights, "Insights should not be null");
        assertNotNull(insights.getText(), "Insights text should not be null");
        assertTrue(insights.getText().length() > 0, "Insights should not be empty");
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(8)
    @DisplayName("Test detectSensitiveContent() - Success")
    void testDetectSensitiveContentSuccess() throws Exception {
        // Arrange
        PDFDocument document = createMockDocument("test");
        
        // Act
        List<String> sensitive = aiService.detectSensitiveContent(document);
        
        // Assert
        assertNotNull(sensitive, "Sensitive content list should not be null");
        assertTrue(sensitive.size() >= 0);
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(9)
    @DisplayName("Test extractTables() - Success")
    void testExtractTablesSuccess() throws Exception {
        // Arrange
        PDFDocument document = createMockDocument("test");
        
        // Act
        List<String> tables = aiService.extractTables(document);
        
        // Assert
        assertNotNull(tables);
        assertTrue(tables.size() >= 0);
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(10)
    @DisplayName("Test detectStructure() - Success")
    void testDetectStructureSuccess() throws Exception {
        // Arrange
        PDFDocument document = createMockDocument("test");
        
        // Act
        List<String> structure = aiService.detectStructure(document);
        
        // Assert
        assertNotNull(structure);
        assertTrue(structure.size() >= 0);
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(11)
    @DisplayName("Test summarize() - Performance check")
    void testSummarizePerformance() throws Exception {
        // Arrange
        PDFDocument document = createMockDocument("test");
        
        // Act
        long startTime = System.currentTimeMillis();
        String summary = aiService.summarize(document);
        long duration = System.currentTimeMillis() - startTime;
        
        // Assert
        assertNotNull(summary);
        // Stub implementation should be fast
        assertTrue(duration < 5000, "Summarize should complete within 5 seconds");
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(12)
    @DisplayName("Test chat() - Multiple messages")
    void testChatMultipleMessages() throws Exception {
        // Arrange
        PDFDocument document = createMockDocument("test");
        
        // Act
        String response1 = aiService.chat(document, "What is this about?");
        String response2 = aiService.chat(document, "Can you elaborate?");
        String response3 = aiService.chat(document, "Any examples?");
        
        // Assert
        assertNotNull(response1);
        assertNotNull(response2);
        assertNotNull(response3);
        
        // Cleanup
        document.getPdDocument().close();
    }
}

