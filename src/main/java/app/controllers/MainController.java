package app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.embed.swing.SwingFXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.pdfbox.rendering.PDFRenderer;

import app.App;
import app.model.PDFDocument;
import app.services.pdf.PDFService;
import app.services.pdf.PDFServiceImpl;
import app.services.ai.AIService;
import app.services.ai.AIServiceImpl;
import app.services.ocr.OCRService;
import app.services.ocr.OCRServiceImpl;

import java.io.File;
import java.util.List;
import java.awt.image.BufferedImage;

/**
 * Main Controller for PDF Super Studio Pro AI
 * Handles all UI interactions in the main window
 */
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    // Services
    private PDFService pdfService;
    private AIService aiService;
    private OCRService ocrService;

    // Current document
    private PDFDocument currentDocument;

    // FXML Components - Top Bar
    @FXML private MenuItem menuItemOpen;
    @FXML private MenuItem menuItemSave;
    @FXML private MenuItem menuItemExit;
    @FXML private CheckMenuItem menuItemDarkMode;

    // FXML Components - Ribbon
    @FXML private Button ribbonHome;
    @FXML private Button ribbonEdit;
    @FXML private Button ribbonConvert;
    @FXML private Button ribbonAI;
    @FXML private ToolBar ribbonContent;

    // FXML Components - Sidebars
    @FXML private VBox leftSidebar;
    @FXML private VBox rightSidebar;
    @FXML private TabPane rightSidebarTabs;

    // FXML Components - PDF Viewer
    @FXML private StackPane pdfViewerPanel;
    @FXML private Label welcomeLabel;
    @FXML private ScrollPane pdfScrollPane;
    @FXML private VBox pdfPagesContainer;

    // FXML Components - AI Features
    @FXML private TextArea aiSummaryText;
    @FXML private VBox chatMessagesContainer;
    @FXML private TextField chatInput;
    @FXML private ComboBox<String> ocrLanguageCombo;
    @FXML private TextArea ocrResultsText;
    @FXML private Label ocrConfidenceLabel;
    @FXML private TextArea insightsText;

    // FXML Components - Status Bar
    @FXML private HBox statusBar;
    @FXML private Label statusLabel;
    @FXML private Label pageInfoLabel;
    @FXML private Label zoomLabel;
    @FXML private ProgressBar progressBar;

    @FXML
    public void initialize() {
        logger.info("Initializing Main Controller...");

        // Initialize services
        pdfService = new PDFServiceImpl();
        aiService = new AIServiceImpl();
        ocrService = new OCRServiceImpl();

        // Initialize OCR language options
        ocrLanguageCombo.getItems().addAll(
            "English", "Spanish", "French", "German", "Italian",
            "Portuguese", "Russian", "Chinese", "Japanese", "Arabic"
        );
        ocrLanguageCombo.getSelectionModel().selectFirst();

        // Set initial theme
        menuItemDarkMode.setSelected(App.getConfigManager().isDarkMode());

        // Set status
        updateStatus("Ready");

        logger.info("Main Controller initialized successfully");
    }

    // File Menu Actions
    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open PDF File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File file = fileChooser.showOpenDialog(getStage());
        if (file != null) {
            openPDF(file);
        }
    }

    @FXML
    private void handleSave() {
        if (currentDocument != null) {
            try {
                pdfService.save(currentDocument, currentDocument.getFile());
                updateStatus("PDF saved successfully");
            } catch (Exception e) {
                logger.error("Error saving PDF", e);
                showError("Save Error", "Failed to save PDF: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSaveAs() {
        if (currentDocument != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF As");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );

            File file = fileChooser.showSaveDialog(getStage());
            if (file != null) {
                try {
                    pdfService.save(currentDocument, file);
                    updateStatus("PDF saved to: " + file.getName());
                } catch (Exception e) {
                    logger.error("Error saving PDF", e);
                    showError("Save Error", "Failed to save PDF: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleExit() {
        getStage().close();
    }

    @FXML
    private void handleNew() {
        // Create new blank PDF
        updateStatus("Creating new PDF...");
    }

    @FXML
    private void handleClose() {
        closePDF();
    }

    @FXML
    private void handlePrint() {
        updateStatus("Print functionality not yet implemented");
    }

    @FXML
    private void handleExport() {
        updateStatus("Export functionality not yet implemented");
    }

    // View Menu Actions
    @FXML
    private void handleToggleTheme() {
        boolean isDarkMode = menuItemDarkMode.isSelected();
        App.getConfigManager().setDarkMode(isDarkMode);
        App.getThemeManager().applyTheme(getStage().getScene(), isDarkMode);
        updateStatus(isDarkMode ? "Dark mode enabled" : "Light mode enabled");
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About PDF Super Studio Pro AI");
        alert.setHeaderText("PDF Super Studio Pro AI v1.0.0");
        alert.setContentText(
            "Enterprise-grade PDF manipulation and AI-powered analysis tool\n\n" +
            "Built with JavaFX 21, Apache PDFBox, Tesseract OCR, and ONNX Runtime\n\n" +
            "© 2025 PDF Super Studio Pro AI"
        );
        alert.showAndWait();
    }

    // Ribbon Actions
    @FXML
    private void handleRibbonHome() {
        updateRibbonContent("Home");
    }

    @FXML
    private void handleRibbonEdit() {
        updateRibbonContent("Edit");
    }

    @FXML
    private void handleRibbonConvert() {
        updateRibbonContent("Convert");
    }

    @FXML
    private void handleRibbonAI() {
        updateRibbonContent("AI Assistant");
    }

    @FXML
    private void handleRibbonView() {
        updateRibbonContent("View");
    }

    @FXML
    private void handleRibbonTools() {
        updateRibbonContent("Tools");
    }

    @FXML
    private void handleRibbonSettings() {
        updateRibbonContent("Settings");
    }

    // Sidebar Actions
    @FXML
    private void handleRecent() {
        updateStatus("Recent files functionality not yet implemented");
    }

    @FXML
    private void handleFavorites() {
        updateStatus("Favorites functionality not yet implemented");
    }

    @FXML
    private void handleCloud() {
        updateStatus("Cloud storage functionality not yet implemented");
    }

    @FXML
    private void handleSearch() {
        updateStatus("Search functionality not yet implemented");
    }

    // AI Features
    @FXML
    private void handleGenerateSummary() {
        if (currentDocument == null) {
            showWarning("No Document", "Please open a PDF document first.");
            return;
        }

        updateStatus("Generating AI summary...");
        progressBar.setVisible(true);

        // Run in background thread
        new Thread(() -> {
            try {
                String summary = aiService.summarize(currentDocument);
                javafx.application.Platform.runLater(() -> {
                    aiSummaryText.setText(summary);
                    updateStatus("Summary generated successfully");
                    progressBar.setVisible(false);
                });
            } catch (Exception e) {
                logger.error("Error generating summary", e);
                javafx.application.Platform.runLater(() -> {
                    showError("AI Error", "Failed to generate summary: " + e.getMessage());
                    progressBar.setVisible(false);
                });
            }
        }).start();
    }

    @FXML
    private void handleSendChatMessage() {
        String message = chatInput.getText().trim();
        if (message.isEmpty() || currentDocument == null) {
            return;
        }

        // Add user message to chat
        addChatMessage("You", message, true);
        chatInput.clear();

        // Get AI response
        new Thread(() -> {
            try {
                String response = aiService.chat(currentDocument, message);
                javafx.application.Platform.runLater(() -> {
                    addChatMessage("AI", response, false);
                });
            } catch (Exception e) {
                logger.error("Error in AI chat", e);
                javafx.application.Platform.runLater(() -> {
                    addChatMessage("AI", "Error: " + e.getMessage(), false);
                });
            }
        }).start();
    }

    @FXML
    private void handleRunOCR() {
        if (currentDocument == null) {
            showWarning("No Document", "Please open a PDF document first.");
            return;
        }

        String language = ocrLanguageCombo.getSelectionModel().getSelectedItem();
        updateStatus("Running OCR with " + language + "...");
        progressBar.setVisible(true);

        new Thread(() -> {
            try {
                var ocrResult = ocrService.performOCR(currentDocument, language);
                javafx.application.Platform.runLater(() -> {
                    ocrResultsText.setText(ocrResult.getText());
                    ocrConfidenceLabel.setText(String.format("Confidence: %.1f%%", 
                        ocrResult.getConfidence() * 100));
                    updateStatus("OCR completed successfully");
                    progressBar.setVisible(false);
                });
            } catch (Exception e) {
                logger.error("Error running OCR", e);
                javafx.application.Platform.runLater(() -> {
                    showError("OCR Error", "Failed to run OCR: " + e.getMessage());
                    progressBar.setVisible(false);
                });
            }
        }).start();
    }

    @FXML
    private void handleExtractEntities() {
        if (currentDocument == null) {
            showWarning("No Document", "Please open a PDF document first.");
            return;
        }

        updateStatus("Extracting entities...");
        new Thread(() -> {
            try {
                List<String> entities = aiService.extractEntities(currentDocument);
                javafx.application.Platform.runLater(() -> {
                    insightsText.setText("Entities:\n" + String.join("\n", entities));
                    updateStatus("Entities extracted successfully");
                });
            } catch (Exception e) {
                logger.error("Error extracting entities", e);
                javafx.application.Platform.runLater(() -> {
                    showError("AI Error", "Failed to extract entities: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleFindTables() {
        updateStatus("Table detection not yet implemented");
    }

    @FXML
    private void handleDetectHeadings() {
        updateStatus("Heading detection not yet implemented");
    }

    // Helper Methods
    private void openPDF(File file) {
        try {
            updateStatus("Loading PDF: " + file.getName());
            progressBar.setVisible(true);

            currentDocument = pdfService.load(file);
            
            welcomeLabel.setVisible(false);
            pdfScrollPane.setVisible(true);

            // Render PDF pages
            renderPDFPages();
            
            pageInfoLabel.setText("Pages: " + currentDocument.getPageCount());
            zoomLabel.setText("100%");

            updateStatus("PDF loaded successfully: " + file.getName());
            progressBar.setVisible(false);

        } catch (Exception e) {
            logger.error("Error opening PDF", e);
            showError("Open Error", "Failed to open PDF: " + e.getMessage());
            progressBar.setVisible(false);
        }
    }
    
    private void renderPDFPages() {
        if (currentDocument == null) {
            return;
        }
        
        logger.info("Rendering PDF pages...");
        pdfPagesContainer.getChildren().clear();
        
        new Thread(() -> {
            try {
                PDFRenderer renderer = new PDFRenderer(currentDocument.getPdDocument());
                int pageCount = currentDocument.getPageCount();
                
                for (int i = 0; i < pageCount; i++) {
                    final int pageIndex = i;
                    
                    // Render page at 150 DPI for good quality
                    BufferedImage bufferedImage = renderer.renderImageWithDPI(pageIndex, 150);
                    Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                    
                    javafx.application.Platform.runLater(() -> {
                        // Create ImageView for the page
                        ImageView imageView = new ImageView(fxImage);
                        imageView.setPreserveRatio(true);
                        imageView.setFitWidth(750); // Default width
                        
                        // Create container for page with border and spacing
                        VBox pageBox = new VBox();
                        pageBox.getStyleClass().add("pdf-page");
                        pageBox.setStyle(
                            "-fx-background-color: white;" +
                            "-fx-border-color: #cccccc;" +
                            "-fx-border-width: 1;" +
                            "-fx-padding: 10;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"
                        );
                        
                        // Add page number label
                        Label pageLabel = new Label("Page " + (pageIndex + 1));
                        pageLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");
                        
                        pageBox.getChildren().addAll(pageLabel, imageView);
                        
                        // Add spacing between pages
                        VBox.setMargin(pageBox, new javafx.geometry.Insets(10, 10, 10, 10));
                        
                        pdfPagesContainer.getChildren().add(pageBox);
                        
                        updateStatus("Rendered page " + (pageIndex + 1) + " of " + pageCount);
                    });
                }
                
                javafx.application.Platform.runLater(() -> {
                    updateStatus("PDF loaded successfully: " + currentDocument.getFile().getName());
                    logger.info("All {} pages rendered successfully", pageCount);
                });
                
            } catch (Exception e) {
                logger.error("Error rendering PDF pages", e);
                javafx.application.Platform.runLater(() -> {
                    showError("Rendering Error", "Failed to render PDF pages: " + e.getMessage());
                });
            }
        }).start();
    }

    private void closePDF() {
        currentDocument = null;
        pdfScrollPane.setVisible(false);
        welcomeLabel.setVisible(true);
        pdfPagesContainer.getChildren().clear();
        pageInfoLabel.setText("");
        aiSummaryText.clear();
        ocrResultsText.clear();
        insightsText.clear();
        chatMessagesContainer.getChildren().clear();
        updateStatus("Ready");
    }

    private void addChatMessage(String sender, String message, boolean isUser) {
        Label messageLabel = new Label(sender + ": " + message);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setStyle(isUser ? 
            "-fx-background-color: -fx-accent; -fx-text-fill: white; -fx-padding: 8; -fx-background-radius: 3;" :
            "-fx-background-color: -fx-background-hover; -fx-padding: 8; -fx-background-radius: 3;"
        );
        chatMessagesContainer.getChildren().add(messageLabel);
    }

    private void updateRibbonContent(String tabName) {
        // Update ribbon content based on selected tab
        // Implementation would dynamically change toolbar buttons
        updateStatus("Switched to " + tabName + " tab");
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
        logger.info("Status: {}", message);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Stage getStage() {
        return App.getPrimaryStage();
    }
}
