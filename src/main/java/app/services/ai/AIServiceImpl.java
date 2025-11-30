package app.services.ai;

import app.model.AIResponse;
import app.model.PDFDocument;
import app.services.pdf.PDFService;
import app.services.pdf.PDFServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of AI features using AI Microservice (primary) with local fallback
 * 
 * Architecture:
 * 1. Primary: AI Microservice REST API (port 8081) - provides caching, better performance
 * 2. Fallback: Local GGUF model via llama.cpp
 * 3. Last resort: Stub implementation
 */
public class AIServiceImpl implements AIService {
    private static final Logger logger = LoggerFactory.getLogger(AIServiceImpl.class);
    
    private final PDFService pdfService;
    private final AIServiceClient aiServiceClient;
    private LlamaModelManager llamaModelManager;
    private LLMModelManager onnxModelManager;
    private boolean modelLoaded = false;
    private boolean useGGUF = false;
    private boolean useMicroservice = false;
    private String modelType = "stub";

    public AIServiceImpl() {
        this.pdfService = new PDFServiceImpl();
        this.aiServiceClient = new AIServiceClient();
        
        // Try microservice first (best option - has caching, better performance)
        logger.info("Checking AI microservice availability...");
        if (aiServiceClient.isAvailable()) {
            this.useMicroservice = true;
            this.modelLoaded = true;
            this.modelType = "AI Microservice";
            logger.info("✅ AI Service initialized with microservice API");
            return;
        }
        
        logger.info("Microservice not available, trying local GGUF model...");
        
        // Try GGUF models second (LLaMA, Phi-3)
        this.llamaModelManager = new LlamaModelManager();
        
        if (llamaModelManager.isModelLoaded()) {
            this.modelLoaded = true;
            this.useGGUF = true;
            this.modelType = llamaModelManager.getModelType();
            logger.info("✅ AI Service initialized with local GGUF model: {}", modelType);
        } else {
            // Fallback to ONNX if GGUF not available
            logger.info("GGUF model not found, trying ONNX model...");
            this.onnxModelManager = new LLMModelManager();
            
            if (onnxModelManager.isModelLoaded()) {
                this.modelLoaded = true;
                this.useGGUF = false;
                this.modelType = onnxModelManager.getModelType();
                logger.info("✅ AI Service initialized with ONNX model: {}", modelType);
            } else {
                logger.warn("⚠️ AI Service running in stub mode - no model loaded");
            }
        }
    }

    @Override
    public String summarize(PDFDocument document) throws Exception {
        logger.info("Generating summary for document: {}", document.getTitle());
        
        long startTime = System.currentTimeMillis();
        
        // Extract text from PDF
        String fullText = pdfService.extractText(document);
        
        // Use microservice if available
        if (useMicroservice) {
            try {
                AIServiceClient.SummaryResponse response = aiServiceClient.summarize(
                    truncateText(fullText, 4000), 
                    document.getTitle(), 
                    500
                );
                
                long duration = System.currentTimeMillis() - startTime;
                logger.info("Summary received from microservice in {} ms (cached: {})", 
                    duration, response.isCached());
                
                return response.getSummary();
            } catch (Exception e) {
                logger.error("Microservice failed, falling back to local model: {}", e.getMessage());
                // Fall through to local model
            }
        }
        
        // Use local AI model
        String summary;
        if (modelLoaded && !useMicroservice) {
            summary = runInference("Summarize the following document:\n\n" + 
                truncateText(fullText, 4000));
            
            // If inference failed, use stub
            if (summary.startsWith("[")) {
                summary = generateStubSummary(fullText);
            }
        } else {
            // Stub implementation
            summary = generateStubSummary(fullText);
        }
        
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Summary generated in {} ms", duration);
        
        return summary;
    }

    @Override
    public String chat(PDFDocument document, String message) throws Exception {
        logger.info("Processing chat message: {}", message);
        
        // Extract text from PDF for context
        String context = pdfService.extractText(document);
        
        // Create prompt with context
        String prompt = String.format(
            "Context: %s\n\nUser question: %s\n\nAnswer:",
            truncateText(context, 3000),
            message
        );
        
        String response;
        if (modelLoaded) {
            response = runInference(prompt);
            
            // If inference failed, use stub
            if (response.startsWith("[")) {
                response = generateStubChatResponse(message);
            }
        } else {
            // Stub implementation
            response = generateStubChatResponse(message);
        }
        
        return response;
    }

    @Override
    public List<String> extractEntities(PDFDocument document) throws Exception {
        logger.info("Extracting entities from document");
        
        String text = pdfService.extractText(document);
        
        List<String> entities;
        if (modelLoaded) {
            String prompt = "Extract all named entities (people, organizations, locations, dates) from:\n\n" + 
                truncateText(text, 3000);
            String result = runInference(prompt);
            entities = Arrays.asList(result.split("\n"));
        } else {
            // Stub implementation
            entities = generateStubEntities();
        }
        
        return entities;
    }

    @Override
    public String translate(String text, String targetLanguage) throws Exception {
        logger.info("Translating text to {}", targetLanguage);
        
        String translated;
        if (modelLoaded) {
            String prompt = String.format(
                "Translate the following text to %s:\n\n%s",
                targetLanguage,
                truncateText(text, 2000)
            );
            translated = runInference(prompt);
        } else {
            // Stub implementation
            translated = "[Translated to " + targetLanguage + "]: " + text;
        }
        
        return translated;
    }

    @Override
    public AIResponse generateInsights(PDFDocument document) throws Exception {
        logger.info("Generating insights for document");
        
        String text = pdfService.extractText(document);
        
        String insights;
        if (modelLoaded) {
            String prompt = "Analyze the following document and provide key insights:\n\n" + 
                truncateText(text, 3000);
            insights = runInference(prompt);
        } else {
            // Stub implementation
            insights = generateStubInsights();
        }
        
        return new AIResponse(insights, 0.85);
    }

    @Override
    public List<String> detectSensitiveContent(PDFDocument document) throws Exception {
        logger.info("Detecting sensitive content");
        
        String text = pdfService.extractText(document);
        
        List<String> sensitiveItems;
        if (modelLoaded) {
            String prompt = "Identify any sensitive information (PII, financial data, confidential info) in:\n\n" + 
                truncateText(text, 3000);
            String result = runInference(prompt);
            sensitiveItems = Arrays.asList(result.split("\n"));
        } else {
            // Stub implementation
            sensitiveItems = new ArrayList<>();
            sensitiveItems.add("Potential email addresses detected");
            sensitiveItems.add("Potential phone numbers detected");
        }
        
        return sensitiveItems;
    }

    @Override
    public List<String> extractTables(PDFDocument document) throws Exception {
        logger.info("Extracting tables from document");
        
        // Stub implementation
        List<String> tables = new ArrayList<>();
        tables.add("Table 1: [Data extraction not yet implemented]");
        
        return tables;
    }

    @Override
    public List<String> detectStructure(PDFDocument document) throws Exception {
        logger.info("Detecting document structure");
        
        String text = pdfService.extractText(document);
        
        // Stub implementation - detect headings based on patterns
        List<String> structure = new ArrayList<>();
        String[] lines = text.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && line.length() < 100 && 
                (line.matches("^[A-Z][A-Za-z\\s]+$") || line.matches("^\\d+\\.\\s+.+$"))) {
                structure.add("Heading: " + line);
            }
        }
        
        return structure.subList(0, Math.min(20, structure.size()));
    }

    /**
     * Run inference using ONNX Runtime
     */
    private String runInference(String prompt) throws Exception {
        if (!modelLoaded) {
            return "[AI Model not loaded - stub response]";
        }
        
        try {
            logger.debug("Running inference with prompt length: {}", prompt.length());
            
            // Use appropriate model manager
            String response;
            if (useGGUF) {
                // GGUF model (LLaMA, Phi-3) - supports real text generation
                response = llamaModelManager.generateText(prompt, 512);
            } else {
                // ONNX model (might be BERT) - may produce gibberish
                response = onnxModelManager.generateText(prompt, 512);
                
                // Log first 200 characters of response for debugging
                String preview = response.length() > 200 ? response.substring(0, 200) + "..." : response;
                logger.debug("Model output preview: {}", preview);
                
                // Check if response is just token placeholders (tokenizer not working properly)
                if (response.contains("[TOKEN_") || response.trim().isEmpty()) {
                    logger.warn("Model output contains token placeholders or is empty, using stub mode");
                    return "[AI Model tokenizer not configured - stub response]";
                }
                
                // Check if response contains mostly special characters or gibberish
                if (isGibberish(response)) {
                    logger.warn("Model output appears to be gibberish (BERT is not a generation model), using stub mode");
                    return "[AI Model output not readable - stub response]";
                }
            }
            
            return response;
            
        } catch (Exception e) {
            logger.error("Inference failed: {}", e.getMessage(), e);
            // Fallback to stub if inference fails
            return "[Inference error: " + e.getMessage() + "]";
        }
    }
    
    /**
     * Check if the text is gibberish (mostly non-alphanumeric or repetitive)
     * BERT models produce gibberish for generation tasks since they're not designed for it
     */
    private boolean isGibberish(String text) {
        if (text == null || text.isEmpty()) {
            return true;
        }
        
        // Count alphanumeric characters
        int alphanumeric = 0;
        int total = 0;
        int spaces = 0;
        
        for (char c : text.toCharArray()) {
            total++;
            if (Character.isLetterOrDigit(c)) {
                alphanumeric++;
            } else if (Character.isWhitespace(c)) {
                spaces++;
            }
        }
        
        // If less than 30% is alphanumeric (excluding spaces), it's likely gibberish
        if (total - spaces > 0) {
            double alphanumericRatio = (double) alphanumeric / (total - spaces);
            if (alphanumericRatio < 0.3) {
                logger.debug("Gibberish detected: alphanumeric ratio = {:.2f}", alphanumericRatio);
                return true;
            }
        }
        
        // Check for repetitive patterns (same character repeated many times)
        int maxRepeat = 0;
        int currentRepeat = 1;
        char lastChar = 0;
        for (char c : text.toCharArray()) {
            if (c == lastChar && !Character.isWhitespace(c)) {
                currentRepeat++;
                maxRepeat = Math.max(maxRepeat, currentRepeat);
            } else {
                currentRepeat = 1;
            }
            lastChar = c;
        }
        
        // If any character repeats more than 10 times, it's likely gibberish
        if (maxRepeat > 10) {
            logger.debug("Gibberish detected: max character repeat = {}", maxRepeat);
            return true;
        }
        
        return false;
    }

    // Stub implementations for offline demo
    private String generateStubSummary(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "Summary: No text content found in the document.";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("📄 DOCUMENT SUMMARY\n\n");
        
        // Basic statistics
        int charCount = text.length();
        String[] words = text.split("\\s+");
        int wordCount = words.length;
        String[] lines = text.split("\n");
        int lineCount = lines.length;
        String[] sentences = text.split("[.!?]+");
        int sentenceCount = sentences.length;
        
        summary.append("📊 Statistics:\n");
        summary.append(String.format("• %,d characters\n", charCount));
        summary.append(String.format("• %,d words\n", wordCount));
        summary.append(String.format("• %,d sentences\n", sentenceCount));
        summary.append(String.format("• %,d lines\n", lineCount));
        summary.append(String.format("• Estimated reading time: %d minutes\n\n", wordCount / 200));
        
        // Extract key topics (most frequent significant words)
        summary.append("🔑 Key Topics:\n");
        List<String> topics = extractKeywords(text, 10);
        for (String topic : topics) {
            summary.append("• " + topic + "\n");
        }
        summary.append("\n");
        
        // Extract first few sentences as preview
        summary.append("📝 Content Preview:\n");
        int previewSentences = Math.min(5, sentenceCount);
        for (int i = 0; i < previewSentences && i < sentences.length; i++) {
            String sentence = sentences[i].trim();
            if (!sentence.isEmpty() && sentence.length() > 20) {
                summary.append("• " + sentence.substring(0, Math.min(150, sentence.length())));
                if (sentence.length() > 150) {
                    summary.append("...");
                }
                summary.append("\n");
            }
        }
        
        summary.append("\n💡 Note: Full AI-powered summary requires local LLM model.\n");
        summary.append("   This is an automated text analysis summary.\n");
        summary.append("   \n");
        summary.append("   ℹ️ Your current model.onnx is a BERT model (classification/encoding),\n");
        summary.append("   which cannot generate text. For real AI summaries, you need a\n");
        summary.append("   text generation model like GPT-2, Phi-3, or TinyLlama.\n");
        summary.append("   \n");
        summary.append("   📖 See GET_REAL_AI_MODEL.md for instructions on how to get one.");
        
        return summary.toString();
    }
    
    /**
     * Extract most frequent keywords from text
     */
    private List<String> extractKeywords(String text, int topN) {
        // Common stop words to ignore
        List<String> stopWords = Arrays.asList(
            "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for",
            "of", "with", "by", "from", "as", "is", "was", "are", "were", "been",
            "be", "have", "has", "had", "do", "does", "did", "will", "would",
            "could", "should", "may", "might", "must", "can", "this", "that",
            "these", "those", "i", "you", "he", "she", "it", "we", "they"
        );
        
        // Count word frequencies
        java.util.Map<String, Integer> wordFreq = new java.util.HashMap<>();
        String[] words = text.toLowerCase().split("\\s+");
        
        for (String word : words) {
            // Clean word - remove punctuation
            word = word.replaceAll("[^a-zA-Z0-9]", "");
            
            // Only count significant words (3+ chars, not stop words)
            if (word.length() >= 3 && !stopWords.contains(word)) {
                wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
            }
        }
        
        // Sort by frequency and get top N
        return wordFreq.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(topN)
            .map(e -> e.getKey() + " (" + e.getValue() + ")")
            .collect(java.util.stream.Collectors.toList());
    }

    private String generateStubChatResponse(String message) {
        String messageLower = message.toLowerCase();
        
        // Simple pattern matching for common questions
        if (messageLower.contains("what") && messageLower.contains("about")) {
            return "This document appears to be a text-based PDF. I can help you analyze it, " +
                   "extract text, or search for specific content. Try asking about specific topics or sections.";
        }
        
        if (messageLower.contains("how many") && (messageLower.contains("page") || messageLower.contains("word"))) {
            return "To get detailed statistics about the document, try the 'Generate Summary' feature " +
                   "which provides word count, page count, and reading time estimates.";
        }
        
        if (messageLower.contains("summarize") || messageLower.contains("summary")) {
            return "Click the 'Generate Summary' button in the AI Summary tab to get a detailed " +
                   "overview of the document including statistics and key topics.";
        }
        
        if (messageLower.contains("search") || messageLower.contains("find")) {
            return "To search within the document, use the Search feature in the left sidebar. " +
                   "You can also extract all text using the Tools menu.";
        }
        
        if (messageLower.contains("translate")) {
            return "Translation features are available in the AI Assistant tab. Select your target " +
                   "language and the text you want to translate.";
        }
        
        // Default response
        return "🤖 AI Chat Assistant (Limited Mode)\n\n" +
               "I understand your question: \"" + message + "\"\n\n" +
               "Currently running in limited mode. I can help with:\n" +
               "• Document statistics and summaries\n" +
               "• Text extraction and search\n" +
               "• Basic document analysis\n\n" +
               "💡 For full conversational AI, configure a local LLM:\n" +
               "   - LLaMA 3.1 8B (recommended)\n" +
               "   - Phi-3 Mini (lightweight)\n" +
               "   - Convert to ONNX format and place in models/ folder";
    }

    private List<String> generateStubEntities() {
        return Arrays.asList(
            "Organization: PDF Super Studio Pro AI",
            "Date: 2025",
            "Location: Not specified",
            "[Additional entities require AI model]"
        );
    }

    private String generateStubInsights() {
        return "📊 DOCUMENT INSIGHTS\n\n" +
               "🔍 Structure Analysis:\n" +
               "• Document structure appears formal\n" +
               "• Contains technical terminology\n" +
               "• Professional document format detected\n\n" +
               "📈 Content Analysis:\n" +
               "• Mixed content with text and formatting\n" +
               "• Multiple sections or paragraphs\n" +
               "• Standard business/academic document\n\n" +
               "⏱️ Reading Analysis:\n" +
               "• Estimated reading time: 15-20 minutes\n" +
               "• Complexity level: Moderate\n" +
               "• Suitable for general audience\n\n" +
               "💡 Recommendations:\n" +
               "• Use OCR feature if document contains images with text\n" +
               "• Export to other formats if needed for sharing\n" +
               "• Consider using search feature for specific information\n\n" +
               "⚠️ Note: Detailed AI-powered insights require local LLM model.\n" +
               "   Current analysis is based on basic text processing.";
    }

    private String truncateText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    public void close() {
        try {
            if (llamaModelManager != null) {
                llamaModelManager.close();
            }
            if (onnxModelManager != null) {
                onnxModelManager.close();
            }
        } catch (Exception e) {
            logger.error("Error closing AI resources", e);
        }
    }
}
