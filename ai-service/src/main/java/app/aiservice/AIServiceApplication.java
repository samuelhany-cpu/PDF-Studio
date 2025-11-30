package app.aiservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * AI Service Microservice Application
 * 
 * Provides REST API endpoints for AI/LLM operations including:
 * - Document summarization
 * - Interactive chat
 * - Entity extraction
 * - Translation
 * - Insights generation
 * 
 * Features:
 * - llama.cpp integration for GGUF models
 * - Async processing support
 * - REST API endpoints
 * - Port conflict detection
 * 
 * Note: Caching disabled to work without Redis/Docker
 */
@SpringBootApplication
@EnableAsync
public class AIServiceApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(AIServiceApplication.class);
    private static final int DEFAULT_PORT = 8081;

    public static void main(String[] args) {
        // Check if port is available before starting
        if (!isPortAvailable(DEFAULT_PORT)) {
            logger.error("╔════════════════════════════════════════════════════════════╗");
            logger.error("║  ERROR: Port {} is already in use!                       ║", DEFAULT_PORT);
            logger.error("║                                                            ║");
            logger.error("║  Another application is using this port.                  ║");
            logger.error("║  Solutions:                                                ║");
            logger.error("║  1. Stop the other application using port {}            ║", DEFAULT_PORT);
            logger.error("║  2. Change port in application.yml: server.port           ║");
            logger.error("║  3. Use command: mvn spring-boot:run -Dserver.port=8082   ║");
            logger.error("╚════════════════════════════════════════════════════════════╝");
            System.exit(1);
        }
        
        logger.info("Port {} is available, starting AI Service...", DEFAULT_PORT);
        SpringApplication.run(AIServiceApplication.class, args);
    }
    
    /**
     * Check if a port is available
     * @param port the port to check
     * @return true if port is available, false otherwise
     */
    private static boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
