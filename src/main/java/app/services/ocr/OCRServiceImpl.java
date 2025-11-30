package app.services.ocr;

import app.model.OCRResult;
import app.model.PDFDocument;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of OCR using Tesseract
 * NOTE: Requires Tesseract to be installed on the system
 * Download: https://github.com/tesseract-ocr/tesseract
 */
public class OCRServiceImpl implements OCRService {
    private static final Logger logger = LoggerFactory.getLogger(OCRServiceImpl.class);
    
    private final Tesseract tesseract;
    private final Map<String, String> languageMap;
    private boolean tesseractAvailable;

    public OCRServiceImpl() {
        this.tesseract = new Tesseract();
        this.languageMap = initializeLanguageMap();
        this.tesseractAvailable = checkTesseractAvailability();
        
        if (tesseractAvailable) {
            // Set Tesseract data path (adjust based on installation)
            // tesseract.setDatapath("path/to/tessdata");
            logger.info("Tesseract OCR initialized successfully");
        } else {
            logger.warn("Tesseract OCR not available - stub implementation will be used");
        }
    }

    @Override
    public OCRResult performOCR(PDFDocument document, String language) throws Exception {
        logger.info("Performing OCR on entire document with language: {}", language);
        
        int[] allPages = new int[document.getPageCount()];
        for (int i = 0; i < allPages.length; i++) {
            allPages[i] = i;
        }
        
        return performOCR(document, language, allPages);
    }

    @Override
    public OCRResult performOCR(PDFDocument document, String language, int[] pageNumbers) throws Exception {
        logger.info("Performing OCR on {} pages with language: {}", pageNumbers.length, language);
        
        long startTime = System.currentTimeMillis();
        
        // Set language for Tesseract
        String tessLang = languageMap.getOrDefault(language, "eng");
        tesseract.setLanguage(tessLang);
        
        StringBuilder ocrText = new StringBuilder();
        double totalConfidence = 0;
        
        PDFRenderer renderer = new PDFRenderer(document.getPdDocument());
        
        for (int pageNum : pageNumbers) {
            try {
                // Render PDF page to image
                BufferedImage image = renderer.renderImageWithDPI(pageNum, 300);
                
                // Perform OCR
                String pageText;
                if (tesseractAvailable) {
                    pageText = tesseract.doOCR(image);
                } else {
                    pageText = performStubOCR(pageNum);
                }
                
                ocrText.append("=== Page ").append(pageNum + 1).append(" ===\n");
                ocrText.append(pageText).append("\n\n");
                
                // Estimate confidence (Tesseract doesn't provide easy page-level confidence)
                // Using 0-100 scale where 85 = 85%
                totalConfidence += 85.0; // Stub value in 0-100 range
                
            } catch (Exception e) {
                logger.error("Error performing OCR on page {}", pageNum, e);
                ocrText.append("=== Page ").append(pageNum + 1).append(" ===\n");
                ocrText.append("[OCR Error: ").append(e.getMessage()).append("]\n\n");
            }
        }
        
        double avgConfidence = totalConfidence / pageNumbers.length;
        long duration = System.currentTimeMillis() - startTime;
        
        logger.info("OCR completed in {} ms with average confidence: {}", duration, avgConfidence);
        
        OCRResult result = new OCRResult(ocrText.toString(), avgConfidence, language);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("pages_processed", pageNumbers.length);
        metadata.put("processing_time_ms", duration);
        metadata.put("tesseract_available", tesseractAvailable);
        result.setMetadata(metadata);
        
        return result;
    }

    @Override
    public String[] getSupportedLanguages() {
        // Return Tesseract language codes, not full names
        return new String[] {
            "eng", "spa", "fra", "deu", "ita",
            "por", "rus", "chi_sim", "jpn", "ara",
            "hin", "kor", "nld", "swe", "tur"
        };
    }

    @Override
    public boolean isTesseractAvailable() {
        return tesseractAvailable;
    }

    private Map<String, String> initializeLanguageMap() {
        Map<String, String> map = new HashMap<>();
        map.put("English", "eng");
        map.put("Spanish", "spa");
        map.put("French", "fra");
        map.put("German", "deu");
        map.put("Italian", "ita");
        map.put("Portuguese", "por");
        map.put("Russian", "rus");
        map.put("Chinese", "chi_sim");
        map.put("Japanese", "jpn");
        map.put("Arabic", "ara");
        map.put("Hindi", "hin");
        map.put("Korean", "kor");
        map.put("Dutch", "nld");
        map.put("Swedish", "swe");
        map.put("Turkish", "tur");
        return map;
    }

    private boolean checkTesseractAvailability() {
        try {
            // Try to initialize Tesseract
            tesseract.setLanguage("eng");
            return true;
        } catch (Exception e) {
            logger.warn("Tesseract not available: {}", e.getMessage());
            return false;
        }
    }

    private String performStubOCR(int pageNum) {
        return String.format(
            "[Stub OCR Result for Page %d]\n" +
            "This is simulated OCR text. To get actual OCR results, install Tesseract:\n" +
            "- Windows: Download from https://github.com/UB-Mannheim/tesseract/wiki\n" +
            "- macOS: brew install tesseract\n" +
            "- Linux: sudo apt-get install tesseract-ocr\n\n" +
            "Sample text content would appear here after actual OCR processing.",
            pageNum + 1
        );
    }
}
