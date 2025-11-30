package app.utils;

import app.model.AppSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manager for application configuration
 */
public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    
    private static final String CONFIG_DIR = System.getProperty("user.home") + 
        File.separator + ".pdfstudio";
    private static final String CONFIG_FILE = CONFIG_DIR + File.separator + "config.json";
    
    private AppSettings settings;
    private final Gson gson;

    private ConfigManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.settings = new AppSettings();
        load();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Load configuration from file
     */
    public void load() {
        try {
            File configFile = new File(CONFIG_FILE);
            
            if (configFile.exists()) {
                try (FileReader reader = new FileReader(configFile)) {
                    settings = gson.fromJson(reader, AppSettings.class);
                    logger.info("Configuration loaded from: {}", CONFIG_FILE);
                }
            } else {
                logger.info("No configuration file found, using defaults");
                save(); // Create default config file
            }
        } catch (Exception e) {
            logger.error("Error loading configuration", e);
            settings = new AppSettings(); // Use defaults
        }
    }

    /**
     * Save configuration to file
     */
    public void save() {
        try {
            // Create config directory if it doesn't exist
            Path configDir = Paths.get(CONFIG_DIR);
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
            
            // Save configuration
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                gson.toJson(settings, writer);
                logger.info("Configuration saved to: {}", CONFIG_FILE);
            }
        } catch (IOException e) {
            logger.error("Error saving configuration", e);
        }
    }

    // Getters and setters
    public boolean isDarkMode() {
        return settings.isDarkMode();
    }

    public void setDarkMode(boolean darkMode) {
        settings.setDarkMode(darkMode);
        save();
    }

    public String getDefaultOCRLanguage() {
        return settings.getDefaultOCRLanguage();
    }

    public void setDefaultOCRLanguage(String language) {
        settings.setDefaultOCRLanguage(language);
        save();
    }

    public String getAiModelPath() {
        return settings.getAiModelPath();
    }

    public void setAiModelPath(String path) {
        settings.setAiModelPath(path);
        save();
    }

    public AppSettings getSettings() {
        return settings;
    }
}
