package app.services.pdf;

import app.model.PDFDocument;

import java.io.File;
import java.util.List;

/**
 * Service interface for PDF operations
 */
public interface PDFService {
    
    /**
     * Load a PDF document from file
     */
    PDFDocument load(File file) throws Exception;
    
    /**
     * Save a PDF document to file
     */
    void save(PDFDocument document, File file) throws Exception;
    
    /**
     * Merge multiple PDF files into one
     */
    PDFDocument merge(List<File> files) throws Exception;
    
    /**
     * Split PDF into separate files
     */
    List<File> split(PDFDocument document, int[] pageRanges) throws Exception;
    
    /**
     * Compress PDF file
     */
    PDFDocument compress(PDFDocument document, double quality) throws Exception;
    
    /**
     * Encrypt PDF with password
     */
    void encrypt(PDFDocument document, String password) throws Exception;
    
    /**
     * Decrypt PDF with password
     */
    void decrypt(PDFDocument document, String password) throws Exception;
    
    /**
     * Convert PDF to images
     */
    List<File> convertToImages(PDFDocument document, String format, int dpi) throws Exception;
    
    /**
     * Convert images to PDF
     */
    PDFDocument convertFromImages(List<File> imageFiles) throws Exception;
    
    /**
     * Optimize PDF for web
     */
    PDFDocument optimize(PDFDocument document) throws Exception;
    
    /**
     * Extract text from PDF
     */
    String extractText(PDFDocument document) throws Exception;
    
    /**
     * Redact text in PDF
     */
    void redact(PDFDocument document, String text) throws Exception;
}
