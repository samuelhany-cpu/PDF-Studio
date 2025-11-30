package app.services.ai;

import ai.onnxruntime.*;
import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.LongBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Manages LLM model loading and inference using ONNX Runtime
 * Supports: LLaMA, Phi-3, GPT-style models in ONNX format
 */
public class LLMModelManager {
    private static final Logger logger = LoggerFactory.getLogger(LLMModelManager.class);
    
    private OrtEnvironment ortEnvironment;
    private OrtSession ortSession;
    private HuggingFaceTokenizer tokenizer;
    private boolean modelLoaded = false;
    private String modelType = "unknown";
    private int maxTokens = 512;
    
    // Model paths to check
    private static final String[] MODEL_PATHS = {
        "models/model.onnx",
        "models/llama/model.onnx",
        "models/phi3/model.onnx",
        "F:/PDF Studio/models/model.onnx",
        System.getProperty("user.home") + "/.pdfstudio/models/model.onnx"
    };
    
    public LLMModelManager() {
        initializeModel();
    }
    
    /**
     * Initialize the ONNX model
     */
    private void initializeModel() {
        try {
            logger.info("Initializing LLM Model Manager...");
            ortEnvironment = OrtEnvironment.getEnvironment();
            
            // Try to find and load model
            String modelPath = findModel();
            
            if (modelPath != null) {
                loadModel(modelPath);
            } else {
                logger.warn("No ONNX model found. Checked locations:");
                for (String path : MODEL_PATHS) {
                    logger.warn("  - {}", path);
                }
                logger.info("To enable AI features, place an ONNX model in one of the above locations");
            }
            
        } catch (Exception e) {
            logger.error("Failed to initialize LLM model", e);
            modelLoaded = false;
        }
    }
    
    /**
     * Find available ONNX model file
     */
    private String findModel() {
        for (String pathStr : MODEL_PATHS) {
            Path path = Paths.get(pathStr);
            if (Files.exists(path)) {
                logger.info("Found model at: {}", pathStr);
                return pathStr;
            }
        }
        return null;
    }
    
    /**
     * Load ONNX model from file
     */
    private void loadModel(String modelPath) {
        try {
            logger.info("Loading ONNX model from: {}", modelPath);
            
            OrtSession.SessionOptions sessionOptions = new OrtSession.SessionOptions();
            
            // Optimize for CPU inference
            sessionOptions.setIntraOpNumThreads(4);
            sessionOptions.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.BASIC_OPT);
            
            // Enable memory pattern optimization
            sessionOptions.setMemoryPatternOptimization(true);
            
            ortSession = ortEnvironment.createSession(modelPath, sessionOptions);
            
            // Initialize Hugging Face tokenizer with BERT vocabulary
            initializeTokenizer();
            
            // Detect model type from metadata or filename
            detectModelType(modelPath);
            
            modelLoaded = true;
            logger.info("✅ Model loaded successfully! Type: {}", modelType);
            
        } catch (OrtException e) {
            logger.error("Failed to load ONNX model: {}", e.getMessage(), e);
            modelLoaded = false;
        } catch (Exception e) {
            logger.error("Unexpected error loading model", e);
            modelLoaded = false;
        }
    }
    
    /**
     * Detect model type from path or metadata
     */
    private void detectModelType(String modelPath) {
        String pathLower = modelPath.toLowerCase();
        
        if (pathLower.contains("llama")) {
            modelType = "LLaMA";
            maxTokens = 2048;
        } else if (pathLower.contains("phi")) {
            modelType = "Phi-3";
            maxTokens = 4096;
        } else if (pathLower.contains("gpt")) {
            modelType = "GPT";
            maxTokens = 1024;
        } else {
            modelType = "Generic";
            maxTokens = 512;
        }
        
        logger.info("Detected model type: {} (max tokens: {})", modelType, maxTokens);
    }
    
    /**
     * Initialize the Hugging Face tokenizer
     * Uses bert-base-uncased tokenizer by default
     */
    private void initializeTokenizer() {
        try {
            logger.info("Initializing BERT tokenizer...");
            
            // Try to load from bert-base-uncased pretrained model
            // This will download the tokenizer files if not already cached
            tokenizer = HuggingFaceTokenizer.newInstance("bert-base-uncased", Map.of(
                "padding", "true",
                "truncation", "true",
                "maxLength", String.valueOf(maxTokens)
            ));
            
            logger.info("✅ BERT tokenizer initialized successfully");
            
        } catch (Exception e) {
            logger.error("Failed to initialize tokenizer: {}", e.getMessage(), e);
            logger.warn("AI features will use fallback stub responses");
            // If tokenizer fails to load, model will still fail gracefully
            tokenizer = null;
        }
    }
    
    /**
     * Run inference on the model
     */
    public String generateText(String prompt, int maxNewTokens) throws OrtException {
        if (!modelLoaded || ortSession == null) {
            throw new IllegalStateException("Model not loaded");
        }
        
        logger.debug("Generating text for prompt (length: {})", prompt.length());
        
        try {
            // Check if tokenizer is available
            if (tokenizer == null) {
                throw new IllegalStateException("Tokenizer not initialized");
            }
            
            // Tokenize input
            Encoding encoding = tokenizer.encode(prompt);
            long[] inputIds = encoding.getIds();
            
            logger.debug("Tokenized input: {} tokens", inputIds.length);
            
            // Truncate if too long (reserve space for generation)
            int maxInputLength = maxTokens - maxNewTokens;
            
            // Ensure we have at least some input tokens
            if (maxInputLength < 128) {
                maxInputLength = Math.min(256, maxTokens / 2);
                maxNewTokens = maxTokens - maxInputLength;
                logger.warn("Adjusted maxInputLength to {} and maxNewTokens to {}", maxInputLength, maxNewTokens);
            }
            
            if (inputIds.length > maxInputLength) {
                // Keep the beginning of the input, not the end
                inputIds = Arrays.copyOfRange(inputIds, 0, maxInputLength);
                logger.debug("Truncated input from {} to {} tokens", inputIds.length, maxInputLength);
            }
            
            logger.debug("Using {} input tokens", inputIds.length);
            
            // Create input tensors
            long[] shape = {1, inputIds.length};
            
            // Input IDs tensor
            LongBuffer inputIdsBuffer = LongBuffer.wrap(inputIds);
            OnnxTensor inputIdsTensor = OnnxTensor.createTensor(ortEnvironment, inputIdsBuffer, shape);
            
            // Attention mask (all 1s for valid tokens)
            long[] attentionMask = new long[inputIds.length];
            Arrays.fill(attentionMask, 1L);
            LongBuffer attentionBuffer = LongBuffer.wrap(attentionMask);
            OnnxTensor attentionMaskTensor = OnnxTensor.createTensor(ortEnvironment, attentionBuffer, shape);
            
            // Token type IDs (all 0s for single sequence)
            long[] tokenTypeIds = new long[inputIds.length];
            Arrays.fill(tokenTypeIds, 0L);
            LongBuffer tokenTypeBuffer = LongBuffer.wrap(tokenTypeIds);
            OnnxTensor tokenTypeIdsTensor = OnnxTensor.createTensor(ortEnvironment, tokenTypeBuffer, shape);
            
            // Run inference with all required inputs
            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put("input_ids", inputIdsTensor);
            inputs.put("attention_mask", attentionMaskTensor);
            inputs.put("token_type_ids", tokenTypeIdsTensor);
            
            OrtSession.Result results = ortSession.run(inputs);
            
            // Get output logits
            OnnxValue outputValue = results.get(0);
            float[][][] logits = (float[][][]) outputValue.getValue();
            
            // Decode output tokens
            String generatedText = decodeOutput(logits, maxNewTokens);
            
            // Cleanup all tensors
            inputIdsTensor.close();
            attentionMaskTensor.close();
            tokenTypeIdsTensor.close();
            results.close();
            
            logger.debug("Generated text length: {}", generatedText.length());
            return generatedText;
            
        } catch (OrtException e) {
            logger.error("Error during inference: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error during inference: {}", e.getMessage(), e);
            throw new RuntimeException("Inference failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Simplified text generation for compatibility
     */
    public String generateText(String prompt) throws OrtException {
        return generateText(prompt, 256);
    }
    
    /**
     * Decode output logits to text (greedy decoding)
     */
    private String decodeOutput(float[][][] logits, int maxTokens) {
        List<Long> outputTokens = new ArrayList<>();
        
        // Simple greedy decoding
        for (int i = 0; i < Math.min(maxTokens, logits[0].length); i++) {
            float[] tokenLogits = logits[0][i];
            
            // Find token with highest probability
            int maxIdx = 0;
            float maxProb = tokenLogits[0];
            
            for (int j = 1; j < tokenLogits.length; j++) {
                if (tokenLogits[j] > maxProb) {
                    maxProb = tokenLogits[j];
                    maxIdx = j;
                }
            }
            
            outputTokens.add((long) maxIdx);
            
            // Stop if end-of-sequence token (BERT [SEP] or common EOS tokens)
            if (maxIdx == 102 || maxIdx == 2 || maxIdx == 50256) {
                break;
            }
        }
        
        // Convert list to array
        long[] tokenArray = outputTokens.stream().mapToLong(Long::longValue).toArray();
        
        // Decode using tokenizer
        if (tokenizer != null) {
            try {
                return tokenizer.decode(tokenArray);
            } catch (Exception e) {
                logger.error("Error decoding tokens: {}", e.getMessage());
                return "[Error decoding output]";
            }
        } else {
            return "[Tokenizer not available]";
        }
    }
    
    /**
     * Check if model is loaded and ready
     */
    public boolean isModelLoaded() {
        return modelLoaded;
    }
    
    /**
     * Get model type
     */
    public String getModelType() {
        return modelType;
    }
    
    /**
     * Get maximum context length
     */
    public int getMaxTokens() {
        return maxTokens;
    }
    
    /**
     * Close and cleanup resources
     */
    public void close() {
        try {
            if (ortSession != null) {
                ortSession.close();
                logger.info("Model session closed");
            }
            if (tokenizer != null) {
                tokenizer.close();
                logger.info("Tokenizer closed");
            }
        } catch (Exception e) {
            logger.error("Error closing model session", e);
        }
    }
}
