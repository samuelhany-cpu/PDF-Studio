package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.utils.ThemeManager;
import app.utils.ConfigManager;

import java.io.IOException;

/**
 * PDF Super Studio Pro AI - Enterprise PDF Manipulation and AI Analysis Tool
 * Main JavaFX Application class
 */
public class App extends Application {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final String APP_TITLE = "PDF Super Studio Pro AI";
    private static final int WINDOW_WIDTH = 1600;
    private static final int WINDOW_HEIGHT = 900;
    
    private static Stage primaryStage;
    private static ThemeManager themeManager;
    private static ConfigManager configManager;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        
        logger.info("Starting PDF Super Studio Pro AI...");
        
        // Initialize managers
        configManager = ConfigManager.getInstance();
        themeManager = ThemeManager.getInstance();
        
        // Load main window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        Parent root = loader.load();
        
        // Create scene
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Apply theme
        themeManager.applyTheme(scene, configManager.isDarkMode());
        
        // Set window properties
        stage.setTitle(APP_TITLE);
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(700);
        
        // Load application icons
        try {
            stage.getIcons().addAll(
                new Image(getClass().getResourceAsStream("/icons/icon512.png")),
                new Image(getClass().getResourceAsStream("/icons/icon256.png")),
                new Image(getClass().getResourceAsStream("/icons/icon128.png"))
            );
        } catch (Exception e) {
            logger.warn("Could not load application icons", e);
        }
        
        // Show window
        stage.show();
        
        logger.info("Application started successfully");
    }

    @Override
    public void stop() throws Exception {
        logger.info("Shutting down PDF Super Studio Pro AI...");
        
        // Save configuration
        configManager.save();
        
        super.stop();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static ThemeManager getThemeManager() {
        return themeManager;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
