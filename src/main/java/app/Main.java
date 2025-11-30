package app;

import javafx.application.Application;

/**
 * Main entry point for PDF Super Studio Pro AI
 * This class launches the JavaFX application
 */
public class Main {
    public static void main(String[] args) {
        // Set system properties for better rendering
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        
        // Launch JavaFX application
        Application.launch(App.class, args);
    }
}
