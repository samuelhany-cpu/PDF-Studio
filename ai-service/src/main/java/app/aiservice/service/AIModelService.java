package app.aiservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Core AI Service for GGUF model inference
 * Uses llama.cpp CLI for text generation
 */
@Service
public class AIModelService {
    private static final Logger logger = LoggerFactory.getLogger(AIModelService.class);
    
    @Value("${ai.model.path}")
    private String modelPath;
    
    @Value("${ai.generation.temperature:0.7}")
    private double temperature;
    
    @Value("${ai.generation.top-k:40}")
    private int topK;
    
    @Value("${ai.generation.top-p:0.9}")
    private double topP;
    
    @Value("${ai.generation.repeat-penalty:1.1}")
    private double repeatPenalty;
    
    @Value("${ai.generation.max-output-tokens:512}")
    private int maxOutputTokens;
    
    private String llamaCppExecutable = null;
    private boolean modelLoaded = false;
    
    // Paths to search for llama.cpp
    private static final String[] LLAMA_CPP_PATHS = {
        "F:/PDF Studio/llama.cpp/llama-cli.exe",
        "llama.cpp/llama-cli.exe",
        "../llama.cpp/llama-cli.exe",
        "llama.cpp/build/bin/Release/llama-cli.exe",
        "C:/llama.cpp/llama-cli.exe",
        "/usr/local/bin/llama-cli",
        "/opt/llama.cpp/llama-cli"
    };
    
    /**
     * Initialize the model and find llama.cpp executable
     */
    public void initialize() {
        try {
            logger.info("Initializing AI Model Service...");
            
            // Check if model file exists
            Path modelFile = Paths.get(modelPath);
            if (!Files.exists(modelFile)) {
                logger.error("Model file not found: {}", modelPath);
                modelLoaded = false;
                return;
            }
            
            logger.info("Found model file: {}", modelPath);
            modelLoaded = true; // Model file exists, so mark as loaded
            
            // Find llama.cpp executable
            llamaCppExecutable = findLlamaCppExecutable();
            
            if (llamaCppExecutable != null) {
                logger.info("✅ llama.cpp found at: {}", llamaCppExecutable);
            } else {
                logger.warn("⚠️ llama.cpp not found. Will use stub responses.");
                logger.warn("Please download llama.cpp from: https://github.com/ggerganov/llama.cpp");
            }
            
        } catch (Exception e) {
            logger.error("Failed to initialize AI Model Service: {}", e.getMessage(), e);
            modelLoaded = false;
        }
    }
    
    /**
     * Generate text using the GGUF model
     */
    public String generateText(String prompt, int maxTokens) {
        try {
            if (llamaCppExecutable != null) {
                return generateViaLlamaCpp(prompt, maxTokens);
            } else {
                return generateStubResponse("llama.cpp not found. Please download and install it.");
            }
        } catch (Exception e) {
            logger.error("Failed to generate text: {}", e.getMessage(), e);
            return generateStubResponse("Error: " + e.getMessage());
        }
    }
    
    /**
     * Generate text via llama.cpp CLI
     */
    private String generateViaLlamaCpp(String prompt, int maxTokens) throws Exception {
        logger.debug("Calling llama.cpp with prompt length: {}", prompt.length());
        
        List<String> command = new ArrayList<>();
        command.add(llamaCppExecutable);
        command.add("-m");
        command.add(modelPath);
        command.add("-p");
        command.add(prompt);
        command.add("-n");
        command.add(String.valueOf(maxTokens));
        command.add("--temp");
        command.add(String.valueOf(temperature));
        command.add("--top-k");
        command.add(String.valueOf(topK));
        command.add("--top-p");
        command.add(String.valueOf(topP));
        command.add("--repeat-penalty");
        command.add(String.valueOf(repeatPenalty));
        command.add("-ngl");
        command.add("0"); // CPU only for now
        
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        
        Process process = processBuilder.start();
        
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            boolean collectingOutput = false;
            
            while ((line = reader.readLine()) != null) {
                // Skip llama.cpp startup messages
                if (line.contains("llama_model_loader") || line.contains("llama_new_context") || 
                    line.contains("ggml_") || line.contains("sampling:")) {
                    continue;
                }
                
                // Start collecting after we see the prompt
                if (!collectingOutput && line.trim().equals(prompt.trim())) {
                    collectingOutput = true;
                    continue;
                }
                
                if (collectingOutput) {
                    output.append(line).append(" ");
                }
            }
        }
        
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("llama.cpp exited with code: " + exitCode);
        }
        
        String result = output.toString().trim();
        logger.debug("Generated {} characters", result.length());
        
        return result.isEmpty() ? generateStubResponse("No output from llama.cpp") : result;
    }
    
    /**
     * Find llama.cpp executable
     */
    private String findLlamaCppExecutable() {
        logger.debug("Searching for llama.cpp in {} paths", LLAMA_CPP_PATHS.length);
        
        for (String pathStr : LLAMA_CPP_PATHS) {
            logger.debug("Checking path: {}", pathStr);
            File file = new File(pathStr);
            if (file.exists()) {
                if (file.canExecute() || pathStr.endsWith(".exe")) {
                    logger.info("Found llama.cpp at: {}", pathStr);
                    return file.getAbsolutePath();
                } else {
                    logger.debug("File exists but is not executable: {}", pathStr);
                }
            } else {
                logger.debug("File not found: {}", pathStr);
            }
        }
        
        logger.warn("llama.cpp executable not found in any of the configured paths");
        return null;
    }
    
    /**
     * Generate a helpful stub response when llama.cpp is not available
     */
    private String generateStubResponse(String reason) {
        return String.format(
            "[AI Service] This is a stub response. Reason: %s\n\n" +
            "To get real AI-generated responses:\n" +
            "1. Download llama.cpp from: https://github.com/ggerganov/llama.cpp\n" +
            "2. Build or download pre-built binaries\n" +
            "3. Place llama-cli.exe in one of the configured paths\n" +
            "4. Restart the AI service",
            reason
        );
    }
    
    public boolean isModelLoaded() {
        return modelLoaded;
    }
    
    public String getModelPath() {
        return modelPath;
    }
}
