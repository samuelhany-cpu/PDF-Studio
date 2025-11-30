package app.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for file operations
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Get file extension
     */
    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(lastDot + 1).toLowerCase() : "";
    }

    /**
     * Get file name without extension
     */
    public static String getFileNameWithoutExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(0, lastDot) : name;
    }

    /**
     * Check if file is a PDF
     */
    public static boolean isPDF(File file) {
        return "pdf".equalsIgnoreCase(getFileExtension(file));
    }

    /**
     * Check if file is an image
     */
    public static boolean isImage(File file) {
        String ext = getFileExtension(file);
        return ext.matches("(png|jpg|jpeg|gif|bmp|tiff)");
    }

    /**
     * Format file size to human-readable string
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Create temporary directory
     */
    public static File createTempDirectory(String prefix) throws IOException {
        Path tempDir = Files.createTempDirectory(prefix);
        logger.info("Created temp directory: {}", tempDir);
        return tempDir.toFile();
    }

    /**
     * Delete directory recursively
     */
    public static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }

        Files.delete(directory.toPath());
        logger.info("Deleted: {}", directory);
    }

    /**
     * Get all files in directory with extension
     */
    public static List<File> getFilesWithExtension(File directory, String extension) {
        try {
            return Files.walk(Paths.get(directory.getAbsolutePath()))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .filter(f -> extension.equalsIgnoreCase(getFileExtension(f)))
                .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Error getting files with extension", e);
            return List.of();
        }
    }

    /**
     * Ensure directory exists
     */
    public static void ensureDirectoryExists(File directory) throws IOException {
        if (!directory.exists()) {
            Files.createDirectories(directory.toPath());
            logger.info("Created directory: {}", directory);
        }
    }
}
