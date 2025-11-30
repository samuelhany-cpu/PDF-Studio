package app.services.ocr;

import app.model.OCRResult;
import app.model.PDFDocument;

/**
 * Service interface for OCR operations
 */
public interface OCRService {
    
    /**
     * Perform OCR on a PDF document
     */
    OCRResult performOCR(PDFDocument document, String language) throws Exception;
    
    /**
     * Perform OCR on specific pages
     */
    OCRResult performOCR(PDFDocument document, String language, int[] pageNumbers) throws Exception;
    
    /**
     * Get list of supported languages
     */
    String[] getSupportedLanguages();
    
    /**
     * Check if Tesseract is properly installed
     */
    boolean isTesseractAvailable();
}
