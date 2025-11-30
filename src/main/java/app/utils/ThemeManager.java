package app.utils;

import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager for application themes
 */
public class ThemeManager {
    private static final Logger logger = LoggerFactory.getLogger(ThemeManager.class);
    private static ThemeManager instance;
    
    private static final String DARK_THEME_PATH = "/themes/dark-theme.css";
    private static final String LIGHT_THEME_PATH = "/themes/light-theme.css";

    private ThemeManager() {
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    /**
     * Apply theme to a scene
     */
    public void applyTheme(Scene scene, boolean darkMode) {
        logger.info("Applying {} theme", darkMode ? "dark" : "light");
        
        // Clear existing stylesheets
        scene.getStylesheets().clear();
        
        // Apply selected theme
        String themePath = darkMode ? DARK_THEME_PATH : LIGHT_THEME_PATH;
        String themeUrl = getClass().getResource(themePath).toExternalForm();
        scene.getStylesheets().add(themeUrl);
        
        logger.info("Theme applied successfully");
    }

    /**
     * Get current theme name
     */
    public String getCurrentThemeName(boolean darkMode) {
        return darkMode ? "Dark" : "Light";
    }
}
