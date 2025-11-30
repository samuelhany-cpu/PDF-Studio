package app.model;

/**
 * Model representing an AI-generated response
 */
public class AIResponse {
    private String text;
    private double confidence;
    private long processingTimeMs;
    private String modelUsed;

    public AIResponse(String text, double confidence) {
        this.text = text;
        this.confidence = confidence;
    }

    public AIResponse(String text, double confidence, long processingTimeMs, String modelUsed) {
        this.text = text;
        this.confidence = confidence;
        this.processingTimeMs = processingTimeMs;
        this.modelUsed = modelUsed;
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

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public String getModelUsed() {
        return modelUsed;
    }

    public void setModelUsed(String modelUsed) {
        this.modelUsed = modelUsed;
    }
}
