package app.model;

/**
 * Model representing application settings
 */
public class AppSettings {
    private boolean darkMode;
    private String defaultOCRLanguage;
    private String aiModelPath;
    private boolean autoSave;
    private int autoSaveIntervalMinutes;
    private double defaultZoom;
    private boolean showLeftSidebar;
    private boolean showRightSidebar;
    private boolean showStatusBar;

    public AppSettings() {
        // Default values
        this.darkMode = true;
        this.defaultOCRLanguage = "English";
        this.aiModelPath = "";
        this.autoSave = false;
        this.autoSaveIntervalMinutes = 5;
        this.defaultZoom = 1.0;
        this.showLeftSidebar = true;
        this.showRightSidebar = true;
        this.showStatusBar = true;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public String getDefaultOCRLanguage() {
        return defaultOCRLanguage;
    }

    public void setDefaultOCRLanguage(String defaultOCRLanguage) {
        this.defaultOCRLanguage = defaultOCRLanguage;
    }

    public String getAiModelPath() {
        return aiModelPath;
    }

    public void setAiModelPath(String aiModelPath) {
        this.aiModelPath = aiModelPath;
    }

    public boolean isAutoSave() {
        return autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public int getAutoSaveIntervalMinutes() {
        return autoSaveIntervalMinutes;
    }

    public void setAutoSaveIntervalMinutes(int autoSaveIntervalMinutes) {
        this.autoSaveIntervalMinutes = autoSaveIntervalMinutes;
    }

    public double getDefaultZoom() {
        return defaultZoom;
    }

    public void setDefaultZoom(double defaultZoom) {
        this.defaultZoom = defaultZoom;
    }

    public boolean isShowLeftSidebar() {
        return showLeftSidebar;
    }

    public void setShowLeftSidebar(boolean showLeftSidebar) {
        this.showLeftSidebar = showLeftSidebar;
    }

    public boolean isShowRightSidebar() {
        return showRightSidebar;
    }

    public void setShowRightSidebar(boolean showRightSidebar) {
        this.showRightSidebar = showRightSidebar;
    }

    public boolean isShowStatusBar() {
        return showStatusBar;
    }

    public void setShowStatusBar(boolean showStatusBar) {
        this.showStatusBar = showStatusBar;
    }
}
