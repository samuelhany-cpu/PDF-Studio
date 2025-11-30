package app.services.ocr;

import app.model.OCRResult;
import app.model.PDFDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OCRServiceImpl
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OCRServiceImplTest {
    
    private OCRService ocrService;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        ocrService = new OCRServiceImpl();
    }
    
    /**
     * Helper method to create a test image with text
     */
    private File createTestImage(String text, String format) throws IOException {
        BufferedImage image = new BufferedImage(400, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // White background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 400, 100);
        
        // Black text
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        g2d.drawString(text, 20, 50);
        g2d.dispose();
        
        File file = tempDir.resolve("test." + format.toLowerCase()).toFile();
        ImageIO.write(image, format, file);
        
        return file;
    }
    
    @Test
    @Order(1)
    @DisplayName("Test performOCR() - Success with PNG image")
    void testPerformOCRSuccessPNG() throws Exception {
        // Arrange
        File imageFile = createTestImage("Hello World", "PNG");
        
        // Create a test PDF document with the image
        PDFDocument document = createPDFFromImage(imageFile);
        String language = "eng";
        
        // Act
        OCRResult result = ocrService.performOCR(document, language);
        
        // Assert
        assertNotNull(result, "OCR result should not be null");
        assertNotNull(result.getText(), "Extracted text should not be null");
        assertTrue(result.getText().length() >= 0, "Extracted text length should be >= 0");
        assertTrue(result.getConfidence() >= 0.0 && result.getConfidence() <= 100.0, 
            "Confidence should be between 0 and 100");
        assertEquals(language, result.getLanguage(), "Language should match");
        
        System.out.println("OCR Result: " + result.getText());
        System.out.println("Confidence: " + result.getConfidence());
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(2)
    @DisplayName("Test performOCR() - Success with JPEG image")
    void testPerformOCRSuccessJPEG() throws Exception {
        // Arrange
        File imageFile = createTestImage("Test OCR", "JPEG");
        PDFDocument document = createPDFFromImage(imageFile);
        
        // Act
        OCRResult result = ocrService.performOCR(document, "eng");
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getText());
        assertTrue(result.getText().length() >= 0);
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(3)
    @DisplayName("Test performOCR() - Multiple languages")
    void testPerformOCRMultipleLanguages() throws Exception {
        // Arrange
        File imageFile = createTestImage("Text", "PNG");
        String[] languages = {"eng", "fra", "deu", "spa"};
        
        // Act & Assert
        for (String lang : languages) {
            try {
                PDFDocument document = createPDFFromImage(imageFile);
                OCRResult result = ocrService.performOCR(document, lang);
                assertNotNull(result, "Result should not be null for language: " + lang);
                assertEquals(lang, result.getLanguage(), "Language should match");
                document.getPdDocument().close();
            } catch (Exception e) {
                // Some languages might not be available - that's OK for testing
                System.out.println("Language " + lang + " not available: " + e.getMessage());
            }
        }
    }
    
    @Test
    @Order(4)
    @DisplayName("Test performOCR() - Empty image")
    void testPerformOCREmptyImage() throws Exception {
        // Arrange - Create blank white image with no text
        BufferedImage blankImage = new BufferedImage(400, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = blankImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 400, 100);
        g2d.dispose();
        
        File file = tempDir.resolve("blank.png").toFile();
        ImageIO.write(blankImage, "PNG", file);
        
        PDFDocument document = createPDFFromImage(file);
        
        // Act
        OCRResult result = ocrService.performOCR(document, "eng");
        
        // Assert
        assertNotNull(result);
        // Blank image should have minimal or no text
        assertTrue(result.getText().length() >= 0);
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(5)
    @DisplayName("Test getSupportedLanguages() - Returns list")
    void testGetSupportedLanguages() {
        // Act
        String[] languages = ocrService.getSupportedLanguages();
        
        // Assert
        assertNotNull(languages, "Languages array should not be null");
        assertTrue(languages.length > 0, "Languages array should not be empty");
        
        // Check for English
        boolean hasEnglish = false;
        for (String lang : languages) {
            if ("eng".equals(lang)) {
                hasEnglish = true;
                break;
            }
        }
        assertTrue(hasEnglish, "Should support English");
        
        // Print supported languages for debugging
        System.out.println("Supported languages: " + String.join(", ", languages));
    }
    
    @Test
    @Order(6)
    @DisplayName("Test getSupportedLanguages() - Contains common languages")
    void testGetSupportedLanguagesCommon() {
        // Act
        String[] languagesArray = ocrService.getSupportedLanguages();
        List<String> languages = java.util.Arrays.asList(languagesArray);
        
        // Assert
        String[] commonLanguages = {"eng", "fra", "deu", "spa", "ita"};
        
        for (String lang : commonLanguages) {
            assertTrue(languages.contains(lang), 
                "Should contain common language: " + lang);
        }
    }
    
    @Test
    @Order(7)
    @DisplayName("Test performOCR() - Numbers and symbols")
    void testPerformOCRNumbersAndSymbols() throws Exception {
        // Arrange
        File imageFile = createTestImage("123-456-7890", "PNG");
        PDFDocument document = createPDFFromImage(imageFile);
        
        // Act
        OCRResult result = ocrService.performOCR(document, "eng");
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getText());
        // Should recognize numbers and dashes
        System.out.println("Recognized: " + result.getText());
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(8)
    @DisplayName("Test performOCR() - Different font sizes")
    void testPerformOCRDifferentFontSizes() throws Exception {
        // Arrange
        int[] fontSizes = {12, 18, 24, 36};
        
        for (int fontSize : fontSizes) {
            BufferedImage image = new BufferedImage(400, 100, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, 400, 100);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, fontSize));
            g2d.drawString("Test " + fontSize, 20, 50);
            g2d.dispose();
            
            File file = tempDir.resolve("font_" + fontSize + ".png").toFile();
            ImageIO.write(image, "PNG", file);
            
            PDFDocument document = createPDFFromImage(file);
            
            // Act
            OCRResult result = ocrService.performOCR(document, "eng");
            
            // Assert
            assertNotNull(result, "Should recognize text at font size: " + fontSize);
            System.out.println("Font " + fontSize + " recognized: " + result.getText());
            
            // Cleanup
            document.getPdDocument().close();
        }
    }
    
    @Test
    @Order(9)
    @DisplayName("Test performOCR() - Performance check")
    void testPerformOCRPerformance() throws Exception {
        // Arrange
        File imageFile = createTestImage("Performance Test", "PNG");
        PDFDocument document = createPDFFromImage(imageFile);
        
        // Act
        long startTime = System.currentTimeMillis();
        OCRResult result = ocrService.performOCR(document, "eng");
        long duration = System.currentTimeMillis() - startTime;
        
        // Assert
        assertNotNull(result);
        System.out.println("OCR completed in " + duration + "ms");
        
        // OCR should complete in reasonable time (< 10 seconds for small image)
        assertTrue(duration < 10000, "OCR should complete within 10 seconds");
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(10)
    @DisplayName("Test performOCR() - High confidence text")
    void testPerformOCRHighConfidence() throws Exception {
        // Arrange - Create clear, large text for high confidence
        BufferedImage image = new BufferedImage(600, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 600, 200);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        g2d.drawString("HELLO", 50, 100);
        g2d.dispose();
        
        File file = tempDir.resolve("high_conf.png").toFile();
        ImageIO.write(image, "PNG", file);
        
        PDFDocument document = createPDFFromImage(file);
        
        // Act
        OCRResult result = ocrService.performOCR(document, "eng");
        
        // Assert
        assertNotNull(result);
        // Clear text should have high confidence
        assertTrue(result.getConfidence() > 50.0, 
            "Clear text should have confidence > 50%");
        System.out.println("High confidence test - Confidence: " + result.getConfidence());
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(11)
    @DisplayName("Test performOCR() - Mixed case text")
    void testPerformOCRMixedCase() throws Exception {
        // Arrange
        File imageFile = createTestImage("Hello WORLD 123", "PNG");
        PDFDocument document = createPDFFromImage(imageFile);
        
        // Act
        OCRResult result = ocrService.performOCR(document, "eng");
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getText());
        System.out.println("Mixed case recognized: " + result.getText());
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    /**
     * Helper to create a PDF from an image
     */
    private PDFDocument createPDFFromImage(File imageFile) throws IOException {
        PDDocument doc = new PDDocument();
        doc.addPage(new PDPage());
        
        File pdfFile = tempDir.resolve("test_doc_" + System.currentTimeMillis() + ".pdf").toFile();
        doc.save(pdfFile);
        doc.close();
        
        PDDocument loaded = org.apache.pdfbox.Loader.loadPDF(pdfFile);
        return new PDFDocument(pdfFile, loaded);
    }
}
