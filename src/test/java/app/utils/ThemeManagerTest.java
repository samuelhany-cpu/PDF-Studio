package app.utils;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ThemeManager
 * Tests theme loading and application
 * Note: Some tests require JavaFX toolkit initialization
 */
@DisplayName("ThemeManager Tests")
class ThemeManagerTest {
    
    private ThemeManager themeManager;
    
    @BeforeEach
    void setUp() {
        themeManager = ThemeManager.getInstance();
    }
    
    @Test
    @DisplayName("Should get singleton instance")
    void testGetInstance() {
        ThemeManager instance1 = ThemeManager.getInstance();
        ThemeManager instance2 = ThemeManager.getInstance();
        assertSame(instance1, instance2, "Should return same singleton instance");
    }
    
    @Test
    @DisplayName("Should get dark theme name")
    void testGetDarkThemeName() {
        String themeName = themeManager.getCurrentThemeName(true);
        assertEquals("Dark", themeName, "Should return Dark theme name");
    }
    
    @Test
    @DisplayName("Should get light theme name")
    void testGetLightThemeName() {
        String themeName = themeManager.getCurrentThemeName(false);
        assertEquals("Light", themeName, "Should return Light theme name");
    }
    
    @Test
    @DisplayName("Should return consistent theme names")
    void testThemeNameConsistency() {
        String dark1 = themeManager.getCurrentThemeName(true);
        String dark2 = themeManager.getCurrentThemeName(true);
        String light1 = themeManager.getCurrentThemeName(false);
        String light2 = themeManager.getCurrentThemeName(false);
        
        assertEquals(dark1, dark2, "Dark theme name should be consistent");
        assertEquals(light1, light2, "Light theme name should be consistent");
        assertNotEquals(dark1, light1, "Dark and light theme names should be different");
    }
    
    @Test
    @DisplayName("Should handle theme resource paths")
    void testThemeResourcePathsExist() {
        // Test that theme CSS files exist in resources
        assertNotNull(getClass().getResource("/themes/dark-theme.css"), 
                     "Dark theme CSS should exist");
        assertNotNull(getClass().getResource("/themes/light-theme.css"), 
                     "Light theme CSS should exist");
    }
}
