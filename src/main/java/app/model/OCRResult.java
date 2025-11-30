package app.model;

import java.util.Map;

/**
 * Model representing OCR results
 */
public class OCRResult {
    private String text;
    private double confidence;
    private String language;
    private Map<String, Object> metadata;

    public OCRResult(String text, double confidence, String language) {
        this.text = text;
        this.confidence = confidence;
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
