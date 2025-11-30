package app.services.ai;

import app.model.AIResponse;
import app.model.PDFDocument;

import java.util.List;

/**
 * Service interface for AI-powered features
 * Uses local ONNX Runtime with LLaMA model for offline inference
 */
public interface AIService {
    
    /**
     * Generate a summary of the PDF document
     */
    String summarize(PDFDocument document) throws Exception;
    
    /**
     * Chat with AI about the PDF content
     */
    String chat(PDFDocument document, String message) throws Exception;
    
    /**
     * Extract structured data from PDF
     */
    List<String> extractEntities(PDFDocument document) throws Exception;
    
    /**
     * Translate selected text
     */
    String translate(String text, String targetLanguage) throws Exception;
    
    /**
     * Generate insights from PDF
     */
    AIResponse generateInsights(PDFDocument document) throws Exception;
    
    /**
     * Detect sensitive content
     */
    List<String> detectSensitiveContent(PDFDocument document) throws Exception;
    
    /**
     * Extract tables from PDF
     */
    List<String> extractTables(PDFDocument document) throws Exception;
    
    /**
     * Detect document structure (headings, paragraphs, etc.)
     */
    List<String> detectStructure(PDFDocument document) throws Exception;
}
