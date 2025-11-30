package app.services.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages GGUF model loading and inference using llama.cpp
 * Supports LLaMA, Phi-3, and other GGUF format models
 */
public class LlamaModelManager {
    private static final Logger logger = LoggerFactory.getLogger(LlamaModelManager.class);
    
    private boolean modelLoaded = false;
    private String modelPath = null;
    private String modelType = "unknown";
    private int maxTokens = 2048;
    private String llamaCppPath = null;
    
    // Paths to check for GGUF models
    private static final String[] MODEL_PATHS = {
        "models/Llama-3.2-3B-Instruct-Q6_K_L_2.gguf",
        "models/phi-3-mini-4k-instruct-q4.gguf",
        "Models/Llama-3.2-3B-Instruct-Q6_K_L_2.gguf",
        "Models/phi-3-mini-4k-instruct-q4.gguf",
        "F:/PDF Studio/models/Llama-3.2-3B-Instruct-Q6_K_L_2.gguf",
        "F:/PDF Studio/Models/Llama-3.2-3B-Instruct-Q6_K_L_2.gguf"
    };
    
    public LlamaModelManager() {
        initializeModel();
    }
    
    private void initializeModel() {
        try {
            logger.info("Initializing LlamaModelManager for GGUF models...");
            
            // Find GGUF model
            modelPath = findGGUFModel();
            
            if (modelPath != null) {
                detectModelType(modelPath);
                modelLoaded = true;
                logger.info("✅ GGUF model found and ready: {} (type: {})", modelPath, modelType);
            } else {
                logger.warn("No GGUF model found. Checked locations:");
                for (String path : MODEL_PATHS) {
                    logger.warn("  - {}", path);
                }
            }
            
        } catch (Exception e) {
            logger.error("Failed to initialize GGUF model: {}", e.getMessage(), e);
            modelLoaded = false;
        }
    }
    
    private String findGGUFModel() {
        for (String pathStr : MODEL_PATHS) {
            Path path = Paths.get(pathStr);
            if (Files.exists(path)) {
                logger.info("Found GGUF model at: {}", pathStr);
                return path.toAbsolutePath().toString();
            }
        }
        return null;
    }
    
    private void detectModelType(String path) {
        String pathLower = path.toLowerCase();
        
        if (pathLower.contains("llama-3.2")) {
            modelType = "LLaMA 3.2 3B";
            maxTokens = 4096;
        } else if (pathLower.contains("llama")) {
            modelType = "LLaMA";
            maxTokens = 4096;
        } else if (pathLower.contains("phi-3")) {
            modelType = "Phi-3 Mini";
            maxTokens = 4096;
        } else {
            modelType = "Generic GGUF";
            maxTokens = 2048;
        }
        
        logger.info("Detected model type: {} (max tokens: {})", modelType, maxTokens);
    }
    
    /**
     * Generate text using the GGUF model via llama.cpp CLI
     */
    public String generateText(String prompt, int maxNewTokens) throws Exception {
        if (!modelLoaded || modelPath == null) {
            throw new IllegalStateException("GGUF model not loaded");
        }
        
        logger.debug("Generating text with GGUF model for prompt length: {}", prompt.length());
        
        try {
            // Use llama-server if available, otherwise use llama-cli
            String response = generateViaLlamaCpp(prompt, maxNewTokens);
            
            if (response != null && !response.trim().isEmpty()) {
                logger.debug("Generated response length: {}", response.length());
                return response;
            } else {
                logger.warn("llama.cpp returned empty response, falling back to stub");
                return generateStubResponse(prompt);
            }
            
        } catch (Exception e) {
            logger.error("Error during GGUF inference: {}", e.getMessage(), e);
            return generateStubResponse(prompt);
        }
    }
    
    /**
     * Generate text via llama.cpp CLI
     */
    private String generateViaLlamaCpp(String prompt, int maxNewTokens) throws Exception {
        // Look for llama.cpp executables
        String llamaExe = findLlamaCppExecutable();
        
        if (llamaExe == null) {
            logger.warn("llama.cpp executable not found, using stub response");
            return null;
        }
        
        logger.info("Using llama.cpp at: {}", llamaExe);
        
        // Write prompt to temporary file to handle UTF-8 encoding properly
        File tempPromptFile = null;
        try {
            tempPromptFile = File.createTempFile("llama_prompt_", ".txt");
            tempPromptFile.deleteOnExit(); // Backup deletion on JVM exit
            
            try (java.io.FileWriter writer = new java.io.FileWriter(tempPromptFile, java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write(prompt);
            }
            
            logger.debug("Wrote prompt to temp file: {}", tempPromptFile.getAbsolutePath());
        } catch (java.io.IOException e) {
            logger.error("Failed to create temporary prompt file: {}", e.getMessage(), e);
            throw new Exception("Unable to create temp file for prompt: " + e.getMessage(), e);
        }
        
        // Build command using --file instead of -p to avoid encoding issues
        List<String> command = new ArrayList<>();
        command.add(llamaExe);
        command.add("-m");
        command.add(modelPath);
        command.add("--file");
        command.add(tempPromptFile.getAbsolutePath());
        command.add("-n");
        command.add(String.valueOf(maxNewTokens));
        command.add("--temp");
        command.add("0.7");
        command.add("--top-k");
        command.add("40");
        command.add("--top-p");
        command.add("0.9");
        command.add("--repeat-penalty");
        command.add("1.1");
        command.add("-ngl");
        command.add("0"); // CPU only, change to 33 for GPU
        
        logger.debug("Running command: {}", String.join(" ", command));
        
        String result = null;
        try {
            // Execute llama.cpp
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            // Read output with UTF-8 encoding
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
                String line;
                boolean captureOutput = false;
                
                while ((line = reader.readLine()) != null) {
                    // Skip llama.cpp startup messages
                    if (line.contains("llama_model_load") || line.contains("llm_load_") || 
                        line.contains("llama_new_context") || line.startsWith("system_info:") ||
                        line.contains("load_backend") || line.contains("error:")) {
                        continue;
                    }
                    
                    // Start capturing after prompt is echoed
                    if (captureOutput || (!line.trim().isEmpty() && !line.startsWith(">"))) {
                        captureOutput = true;
                        output.append(line).append("\n");
                    }
                }
            }
            
            // Wait for process to complete
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                logger.warn("llama.cpp exited with code: {}", exitCode);
            }
            
            result = output.toString().trim();
        } finally {
            // Always clean up temp file, even if exception occurs
            if (tempPromptFile != null && tempPromptFile.exists()) {
                boolean deleted = tempPromptFile.delete();
                if (!deleted) {
                    logger.warn("Failed to delete temp file: {}", tempPromptFile.getAbsolutePath());
                } else {
                    logger.debug("Cleaned up temp file: {}", tempPromptFile.getAbsolutePath());
                }
            }
        }
        
        // Remove prompt echo if present
        if (result.startsWith(prompt)) {
            result = result.substring(prompt.length()).trim();
        }
        
        return result;
    }
    
    /**
     * Find llama.cpp executable
     */
    private String findLlamaCppExecutable() {
        String[] searchPaths = {
            "llama.cpp/build/bin/Release/llama-cli.exe",
            "llama.cpp/build/bin/llama-cli.exe",
            "llama.cpp/llama-cli.exe",
            "llama-cli.exe",
            "llama.exe",
            "C:/llama.cpp/build/bin/Release/llama-cli.exe",
            "C:/llama.cpp/llama-cli.exe",
            "llama.cpp/build/bin/Release/main.exe",
            "llama.cpp/build/bin/main.exe",
            "C:/llama.cpp/build/bin/Release/main.exe"
        };
        
        for (String path : searchPaths) {
            File exe = new File(path);
            if (exe.exists() && exe.canExecute()) {
                logger.info("Found llama.cpp executable: {}", path);
                return exe.getAbsolutePath();
            }
        }
        
        logger.warn("llama.cpp executable not found in standard locations");
        return null;
    }
    
    /**
     * Generate stub response when llama.cpp is not available
     */
    private String generateStubResponse(String prompt) {
        return "🤖 GGUF Model Detected: " + modelType + "\n\n" +
               "Model File: " + new File(modelPath).getName() + "\n" +
               "Max Tokens: " + maxTokens + "\n\n" +
               "⚠️ llama.cpp executable not found!\n\n" +
               "To enable real AI inference:\n" +
               "1. Download llama.cpp from: https://github.com/ggerganov/llama.cpp\n" +
               "2. Build it or download pre-built binaries\n" +
               "3. Place llama-cli.exe in one of these locations:\n" +
               "   - F:/PDF Studio/llama.cpp/build/bin/Release/llama-cli.exe\n" +
               "   - F:/PDF Studio/llama-cli.exe\n" +
               "   - C:/llama.cpp/build/bin/Release/llama-cli.exe\n\n" +
               "Your GGUF model is ready and will work once llama.cpp is available!";
    }
    
    /**
     * Simplified version with default max tokens
     */
    public String generateText(String prompt) throws Exception {
        return generateText(prompt, 512);
    }
    
    public boolean isModelLoaded() {
        return modelLoaded;
    }
    
    public String getModelType() {
        return modelType;
    }
    
    public int getMaxTokens() {
        return maxTokens;
    }
    
    public String getModelPath() {
        return modelPath;
    }
    
    public void close() {
        logger.info("LlamaModelManager closed");
    }
}
