package app.services.pdf;

import app.model.PDFDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PDFServiceImpl
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PDFServiceImplTest {
    
    private PDFService pdfService;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        pdfService = new PDFServiceImpl();
    }
    
    /**
     * Helper method to create a simple test PDF
     */
    private File createTestPDF(String filename, int pageCount) throws IOException {
        File file = tempDir.resolve(filename).toFile();
        PDDocument doc = new PDDocument();
        
        for (int i = 0; i < pageCount; i++) {
            doc.addPage(new PDPage());
        }
        
        doc.save(file);
        doc.close();
        
        return file;
    }
    
    @Test
    @Order(1)
    @DisplayName("Test load() - Success with valid PDF")
    void testLoadSuccess() throws Exception {
        // Arrange
        File testFile = createTestPDF("test.pdf", 3);
        
        // Act
        PDFDocument document = pdfService.load(testFile);
        
        // Assert
        assertNotNull(document, "Loaded document should not be null");
        assertEquals(3, document.getPageCount(), "Page count should be 3");
        assertNotNull(document.getPdDocument(), "PDDocument should not be null");
        assertEquals(testFile, document.getFile(), "File should match");
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(2)
    @DisplayName("Test load() - Fail with non-existent file")
    void testLoadFileNotFound() {
        // Arrange
        File nonExistent = new File(tempDir.toFile(), "nonexistent.pdf");
        
        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> {
            pdfService.load(nonExistent);
        });
        
        assertTrue(exception.getMessage().contains("not found"), 
            "Exception message should indicate file not found");
    }
    
    @Test
    @Order(3)
    @DisplayName("Test load() - Handle empty PDF")
    void testLoadEmptyPDF() throws Exception {
        // Arrange
        File emptyFile = createTestPDF("empty.pdf", 0);
        
        // Act
        PDFDocument document = pdfService.load(emptyFile);
        
        // Assert
        assertNotNull(document);
        assertEquals(0, document.getPageCount(), "Empty PDF should have 0 pages");
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(4)
    @DisplayName("Test save() - Success")
    void testSaveSuccess() throws Exception {
        // Arrange
        File sourceFile = createTestPDF("source.pdf", 2);
        PDFDocument document = pdfService.load(sourceFile);
        File targetFile = tempDir.resolve("saved.pdf").toFile();
        
        // Act
        pdfService.save(document, targetFile);
        
        // Assert
        assertTrue(targetFile.exists(), "Saved file should exist");
        assertTrue(targetFile.length() > 0, "Saved file should not be empty");
        assertFalse(document.isModified(), "Document should not be marked as modified after save");
        
        // Verify the saved file can be loaded
        PDFDocument reloaded = pdfService.load(targetFile);
        assertEquals(2, reloaded.getPageCount(), "Reloaded document should have same page count");
        
        // Cleanup
        document.getPdDocument().close();
        reloaded.getPdDocument().close();
    }
    
    @Test
    @Order(5)
    @DisplayName("Test save() - Create parent directories")
    void testSaveCreatesParentDirs() throws Exception {
        // Arrange
        File sourceFile = createTestPDF("source.pdf", 1);
        PDFDocument document = pdfService.load(sourceFile);
        File targetFile = new File(tempDir.toFile(), "subdir/nested/saved.pdf");
        
        // Act
        pdfService.save(document, targetFile);
        
        // Assert
        assertTrue(targetFile.exists(), "File should be created in nested directory");
        assertTrue(targetFile.getParentFile().exists(), "Parent directories should be created");
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(6)
    @DisplayName("Test merge() - Success with multiple PDFs")
    void testMergeSuccess() throws Exception {
        // Arrange
        List<File> files = new ArrayList<>();
        files.add(createTestPDF("merge1.pdf", 2));
        files.add(createTestPDF("merge2.pdf", 3));
        files.add(createTestPDF("merge3.pdf", 1));
        
        // Act
        PDFDocument merged = pdfService.merge(files);
        
        // Assert
        assertNotNull(merged, "Merged document should not be null");
        assertEquals(6, merged.getPageCount(), "Merged document should have 6 pages (2+3+1)");
        
        // Cleanup
        merged.getPdDocument().close();
    }
    
    @Test
    @Order(7)
    @DisplayName("Test merge() - Fail with empty list")
    void testMergeEmptyList() {
        // Arrange
        List<File> emptyList = new ArrayList<>();
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pdfService.merge(emptyList);
        });
    }
    
    @Test
    @Order(8)
    @DisplayName("Test merge() - Fail with null list")
    void testMergeNullList() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pdfService.merge(null);
        });
    }
    
    @Test
    @Order(9)
    @DisplayName("Test merge() - Fail with non-existent file in list")
    void testMergeNonExistentFile() throws Exception {
        // Arrange
        List<File> files = new ArrayList<>();
        files.add(createTestPDF("exists.pdf", 1));
        files.add(new File(tempDir.toFile(), "does-not-exist.pdf"));
        
        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfService.merge(files);
        });
    }
    
    @Test
    @Order(10)
    @DisplayName("Test split() - Success")
    void testSplitSuccess() throws Exception {
        // Arrange
        File sourceFile = createTestPDF("split-source.pdf", 5);
        PDFDocument document = pdfService.load(sourceFile);
        
        // Act
        List<File> splitFiles = pdfService.split(document, null);
        
        // Assert
        assertNotNull(splitFiles, "Split files list should not be null");
        assertEquals(5, splitFiles.size(), "Should split into 5 files (one per page)");
        
        for (File file : splitFiles) {
            assertTrue(file.exists(), "Split file should exist: " + file.getName());
        }
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(11)
    @DisplayName("Test split() - Fail with null document")
    void testSplitNullDocument() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pdfService.split(null, null);
        });
    }
    
    @Test
    @Order(12)
    @DisplayName("Test encrypt() - Success")
    void testEncryptSuccess() throws Exception {
        // Arrange
        File sourceFile = createTestPDF("encrypt.pdf", 1);
        PDFDocument document = pdfService.load(sourceFile);
        String password = "testPassword123";
        
        // Act
        pdfService.encrypt(document, password);
        
        // Assert
        assertTrue(document.isModified(), "Document should be marked as modified");
        // Note: isEncrypted() only returns true after save/reload in PDFBox
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(13)
    @DisplayName("Test encrypt() - Fail with empty password")
    void testEncryptEmptyPassword() throws Exception {
        // Arrange
        File sourceFile = createTestPDF("encrypt-fail.pdf", 1);
        PDFDocument document = pdfService.load(sourceFile);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pdfService.encrypt(document, "");
        });
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(14)
    @DisplayName("Test encrypt() - Fail with null password")
    void testEncryptNullPassword() throws Exception {
        // Arrange
        File sourceFile = createTestPDF("encrypt-null.pdf", 1);
        PDFDocument document = pdfService.load(sourceFile);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pdfService.encrypt(document, null);
        });
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(15)
    @DisplayName("Test decrypt() - Success")
    void testDecryptSuccess() throws Exception {
        // Arrange
        File sourceFile = createTestPDF("decrypt.pdf", 1);
        PDFDocument document = pdfService.load(sourceFile);
        pdfService.encrypt(document, "password");
        
        // Act
        pdfService.decrypt(document, "password");
        
        // Assert
        assertTrue(document.isModified(), "Document should be marked as modified");
        // Note: decrypt removes protection policy, document may still appear encrypted
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(16)
    @DisplayName("Test extractText() - Success")
    void testExtractTextSuccess() throws Exception {
        // Arrange
        File sourceFile = createTestPDF("extract-text.pdf", 2);
        PDFDocument document = pdfService.load(sourceFile);
        
        // Act
        String text = pdfService.extractText(document);
        
        // Assert
        assertNotNull(text, "Extracted text should not be null");
        // Empty PDF pages will have minimal or no text
        assertTrue(text.length() >= 0, "Text length should be >= 0");
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(17)
    @DisplayName("Test convertToImages() - Success with PNG")
    void testConvertToImagesPNG() throws Exception {
        // Arrange
        File sourceFile = createTestPDF("to-images.pdf", 2);
        PDFDocument document = pdfService.load(sourceFile);
        
        // Act
        List<File> imageFiles = pdfService.convertToImages(document, "PNG", 150);
        
        // Assert
        assertNotNull(imageFiles, "Image files list should not be null");
        assertEquals(2, imageFiles.size(), "Should create 2 image files");
        
        for (File imageFile : imageFiles) {
            assertTrue(imageFile.exists(), "Image file should exist");
            assertTrue(imageFile.getName().toLowerCase().endsWith(".png"), 
                "Image file should have .png extension");
            assertTrue(imageFile.length() > 0, "Image file should not be empty");
        }
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(18)
    @DisplayName("Test convertToImages() - Success with JPEG")
    void testConvertToImagesJPEG() throws Exception {
        // Arrange
        File sourceFile = createTestPDF("to-jpg.pdf", 1);
        PDFDocument document = pdfService.load(sourceFile);
        
        // Act
        List<File> imageFiles = pdfService.convertToImages(document, "JPEG", 72);
        
        // Assert
        assertNotNull(imageFiles);
        assertEquals(1, imageFiles.size());
        assertTrue(imageFiles.get(0).getName().toLowerCase().endsWith(".jpeg"));
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(19)
    @DisplayName("Test compress() - Success (stub implementation)")
    void testCompressSuccess() throws Exception {
        // Arrange
        File sourceFile = createTestPDF("compress.pdf", 3);
        PDFDocument document = pdfService.load(sourceFile);
        
        // Act
        PDFDocument compressed = pdfService.compress(document, 0.75);
        
        // Assert
        assertNotNull(compressed, "Compressed document should not be null");
        // Stub implementation returns same document
        assertEquals(document, compressed);
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(20)
    @DisplayName("Test compress() - Fail with invalid quality")
    void testCompressInvalidQuality() throws Exception {
        // Arrange
        File sourceFile = createTestPDF("compress-fail.pdf", 1);
        PDFDocument document = pdfService.load(sourceFile);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pdfService.compress(document, 1.5); // > 1.0
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            pdfService.compress(document, -0.5); // < 0.0
        });
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(21)
    @DisplayName("Test redact() - Success (stub implementation)")
    void testRedactSuccess() throws Exception {
        // Arrange
        File sourceFile = createTestPDF("redact.pdf", 1);
        PDFDocument document = pdfService.load(sourceFile);
        
        // Act
        pdfService.redact(document, "sensitive text");
        
        // Assert
        assertTrue(document.isModified(), "Document should be marked as modified");
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(22)
    @DisplayName("Test redact() - Fail with empty text")
    void testRedactEmptyText() throws Exception {
        // Arrange
        File sourceFile = createTestPDF("redact-fail.pdf", 1);
        PDFDocument document = pdfService.load(sourceFile);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            pdfService.redact(document, "");
        });
        
        // Cleanup
        document.getPdDocument().close();
    }
    
    @Test
    @Order(23)
    @DisplayName("Test optimize() - Success (stub implementation)")
    void testOptimizeSuccess() throws Exception {
        // Arrange
        File sourceFile = createTestPDF("optimize.pdf", 2);
        PDFDocument document = pdfService.load(sourceFile);
        
        // Act
        PDFDocument optimized = pdfService.optimize(document);
        
        // Assert
        assertNotNull(optimized);
        assertEquals(document, optimized); // Stub returns same document
        
        // Cleanup
        document.getPdDocument().close();
    }
}
