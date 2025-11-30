package app.model;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;

/**
 * Model representing a PDF document in the application
 */
public class PDFDocument {
    private File file;
    private PDDocument pdDocument;
    private int pageCount;
    private String title;
    private String author;
    private boolean modified;

    public PDFDocument(File file, PDDocument pdDocument) {
        this.file = file;
        this.pdDocument = pdDocument;
        this.pageCount = pdDocument.getNumberOfPages();
        this.modified = false;

        // Extract metadata
        var info = pdDocument.getDocumentInformation();
        this.title = info.getTitle() != null ? info.getTitle() : file.getName();
        this.author = info.getAuthor();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public PDDocument getPdDocument() {
        return pdDocument;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.modified = true;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
        this.modified = true;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void close() {
        try {
            if (pdDocument != null) {
                pdDocument.close();
            }
        } catch (Exception e) {
            // Log error
        }
    }
}
