# Testing and Debugging Guide - PDF Super Studio Pro AI

## 📋 Table of Contents
- [Logging System](#logging-system)
- [Running Tests](#running-tests)
- [Test Coverage](#test-coverage)
- [Debugging Guide](#debugging-guide)
- [Performance Analysis](#performance-analysis)
- [Troubleshooting](#troubleshooting)

---

## 🔍 Logging System

### Overview
The application uses Logback for comprehensive logging with multiple appenders and log levels optimized for error detection and debugging.

### Log Files Location
All log files are stored in:
```
Windows: C:\Users\<YourUsername>\.pdfstudio\logs\
macOS/Linux: ~/.pdfstudio/logs/
```

### Log Files

#### 1. **pdf-super-studio.log** - Main Application Log
- **Purpose**: General application logs (INFO level and above)
- **Rotation**: Daily, keeps 30 days
- **Size Cap**: 1 GB total
- **Contents**: All application activity, INFO/WARN/ERROR messages
- **Example**:
```
2025-11-14 02:05:37.256 - INFO - app.services.pdf.PDFServiceImpl - load() - SUCCESS - File: extract-text.pdf, Pages: 2, Duration: 4ms
```

#### 2. **pdf-super-studio-error.log** - Error-Only Log
- **Purpose**: ERROR level messages only for quick problem identification
- **Rotation**: Daily, keeps 90 days (longer retention for debugging)
- **Size Cap**: 500 MB total
- **Contents**: Stack traces, error details, failed operations
- **Example**:
```
2025-11-14 01:50:49.046 - ERROR - app.services.pdf.PDFServiceImpl - load() - File does not exist: nonexistent.pdf
java.io.IOException: File not found: nonexistent.pdf
    at app.services.pdf.PDFServiceImpl.load(PDFServiceImpl.java:37)
    ...
```

#### 3. **pdf-super-studio-performance.log** - Performance Metrics
- **Purpose**: Track operation timings and performance bottlenecks
- **Rotation**: Daily, keeps 30 days
- **Contents**: Execution durations, resource usage metrics
- **Example**:
```
2025-11-14 02:05:37.770 - PDF to Images: 2 pages - 150 DPI - 480ms
2025-11-14 02:05:36.921 - PDF Merge: 3 files -> 6 pages - 155ms
2025-11-14 02:05:37.151 - PDF Encrypt: AES-256 - 22ms
```

### Log Levels by Package

| Package | Level | Purpose |
|---------|-------|---------|
| `app.services.pdf` | DEBUG | Detailed PDF operation logging |
| `app.services.ai` | DEBUG | AI service call logging |
| `app.services.ocr` | DEBUG | OCR process logging |
| `app.controller` | DEBUG | User interaction logging |
| `app.utils` | INFO | Utility function logging |
| `org.apache.pdfbox` | WARN | Suppress verbose PDFBox logs |
| `net.sourceforge.tess4j` | WARN | Suppress verbose Tesseract logs |
| `ai.onnxruntime` | WARN | Suppress verbose ONNX logs |
| `javafx` | INFO | JavaFX framework logs |

### Logging Patterns

#### DEBUG Level - Method Entry/Exit
```java
logger.debug("methodName() - START - param1: {}, param2: {}", value1, value2);
// ... method logic ...
logger.debug("methodName() - Processing step X");
```

#### INFO Level - Success Operations
```java
logger.info("methodName() - SUCCESS - details, Duration: {}ms", duration);
```

#### WARN Level - Non-critical Issues
```java
logger.warn("methodName() - Using STUB implementation");
logger.warn("methodName() - Document is not encrypted");
```

#### ERROR Level - Failures
```java
logger.error("methodName() - FAILED - Duration: {}ms, Error: {}", duration, e.getMessage(), e);
```

#### Performance Logger
```java
perfLogger.info("Operation Description: metrics - {}ms", duration);
```

---

## 🧪 Running Tests

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Internet connection (first run downloads dependencies)

### Run All Tests
```bash
mvn test
```

**Expected Output:**
```
Tests run: 46, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Run Specific Test Class
```bash
# PDF Service Tests (23 tests)
mvn test -Dtest=PDFServiceImplTest

# AI Service Tests (12 tests)
mvn test -Dtest=AIServiceImplTest

# OCR Service Tests (11 tests)
mvn test -Dtest=OCRServiceImplTest
```

### Run Single Test Method
```bash
mvn test -Dtest=PDFServiceImplTest#testLoadSuccess
mvn test -Dtest=AIServiceImplTest#testSummarize
mvn test -Dtest=OCRServiceImplTest#testPerformOCRPNG
```

### Run Tests with Verbose Output
```bash
mvn test -X
```

### Clean and Test
```bash
mvn clean test
```

---

## 📊 Test Coverage

### PDF Service Tests (23 tests) - ✅ All Passing

| Category | Tests | Status |
|----------|-------|--------|
| **Load Operations** | 3 | ✅ Pass |
| - Load success | 1 | ✅ |
| - File not found | 1 | ✅ |
| - Empty PDF | 1 | ✅ |
| **Save Operations** | 2 | ✅ Pass |
| - Save success | 1 | ✅ |
| - Create parent directories | 1 | ✅ |
| **Merge Operations** | 4 | ✅ Pass |
| - Merge success | 1 | ✅ |
| - Empty list | 1 | ✅ |
| - Null list | 1 | ✅ |
| - Non-existent file | 1 | ✅ |
| **Split Operations** | 2 | ✅ Pass |
| - Split success | 1 | ✅ |
| - Null document | 1 | ✅ |
| **Encryption** | 4 | ✅ Pass |
| - Encrypt success | 1 | ✅ |
| - Empty password | 1 | ✅ |
| - Null password | 1 | ✅ |
| - Decrypt | 1 | ✅ |
| **Text Operations** | 1 | ✅ Pass |
| - Extract text | 1 | ✅ |
| **Image Conversion** | 2 | ✅ Pass |
| - To PNG | 1 | ✅ |
| - To JPEG | 1 | ✅ |
| **Compression** | 2 | ✅ Pass |
| - Compress success | 1 | ✅ |
| - Invalid quality | 1 | ✅ |
| **Redaction** | 2 | ✅ Pass |
| - Redact success | 1 | ✅ |
| - Empty text | 1 | ✅ |
| **Optimization** | 1 | ✅ Pass |
| - Optimize | 1 | ✅ |

### AI Service Tests (12 tests) - ✅ All Passing

| Feature | Tests | Status |
|---------|-------|--------|
| Document Summarization | 1 | ✅ |
| Chat/Q&A | 1 | ✅ |
| Entity Extraction | 1 | ✅ |
| Translation | 2 | ✅ |
| Insights Generation | 1 | ✅ |
| Sensitive Content Detection | 2 | ✅ |
| Table Extraction | 2 | ✅ |
| Structure Detection | 2 | ✅ |

### OCR Service Tests (11 tests) - ✅ All Passing

| Feature | Tests | Status |
|---------|-------|--------|
| Image Format Support | 2 | ✅ (PNG, JPEG) |
| Multi-language | 1 | ✅ |
| Empty Images | 1 | ✅ |
| Language Support | 2 | ✅ |
| Numbers/Symbols | 1 | ✅ |
| Different Fonts | 1 | ✅ |
| Performance | 1 | ✅ |
| High Confidence | 1 | ✅ |
| Mixed Case | 1 | ✅ |

### Total Test Count
- **Total**: 46 tests
- **Passing**: 46 (100%)
- **Failing**: 0
- **Execution Time**: ~14 seconds

---

## 🐛 Debugging Guide

### Step 1: Reproduce the Issue
1. Run the specific operation that fails
2. Note the exact error message
3. Check which log files were updated

### Step 2: Check Error Logs
```bash
# Windows
type C:\Users\<YourUsername>\.pdfstudio\logs\pdf-super-studio-error.log

# macOS/Linux
cat ~/.pdfstudio/logs/pdf-super-studio-error.log
```

Look for:
- Stack traces
- Error messages
- Failed operations
- Timestamp of failure

### Step 3: Check Main Application Log
```bash
# View last 50 lines
tail -50 C:\Users\<YourUsername>\.pdfstudio\logs\pdf-super-studio.log

# Search for specific term
findstr /C:"ERROR" pdf-super-studio.log
grep "ERROR" pdf-super-studio.log
```

### Step 4: Check DEBUG Logs
For detailed operation flow, look for DEBUG level messages in the main log:
```
2025-11-14 02:05:37.290 - DEBUG - app.services.pdf.PDFServiceImpl - convertToImages() - START - Format: PNG, DPI: 150, Pages: 2
2025-11-14 02:05:37.290 - DEBUG - app.services.pdf.PDFServiceImpl - convertToImages() - Rendering page 1/2
2025-11-14 02:05:37.540 - DEBUG - app.services.pdf.PDFServiceImpl - convertToImages() - Saved page 1 to: page_1.png (9715 bytes)
```

### Step 5: Common Error Patterns

#### File Not Found Errors
```
ERROR - load() - File does not exist: /path/to/file.pdf
```
**Solution**: Check file path, permissions, file existence

#### Null Pointer Exceptions
```
ERROR - FAILED - Duration: 2ms, Error: Cannot invoke "method()" because "object" is null
```
**Solution**: Check for null input validation, verify objects are initialized

#### PDF Processing Errors
```
ERROR - merge() - File 1 does not exist: /path/to/file.pdf
```
**Solution**: Verify all input files exist before calling merge

#### Tesseract Not Available
```
WARN - Tesseract not available - stub implementation will be used
```
**Solution**: Install Tesseract OCR or accept stub implementation

---

## ⚡ Performance Analysis

### Using Performance Logs

#### 1. View Performance Log
```bash
type C:\Users\<YourUsername>\.pdfstudio\logs\pdf-super-studio-performance.log
```

#### 2. Find Slow Operations
```bash
# Windows - Find operations > 500ms
findstr /C:"ms" pdf-super-studio-performance.log | findstr /R "[5-9][0-9][0-9]ms [0-9][0-9][0-9][0-9]ms"

# Linux/macOS
grep "ms" pdf-super-studio-performance.log | grep -E "(5[0-9]{2}|[6-9][0-9]{2}|[0-9]{4,})ms"
```

#### 3. Performance Benchmarks

| Operation | Expected Time | Notes |
|-----------|---------------|-------|
| Load small PDF (< 10 pages) | < 50ms | Depends on file size |
| Load large PDF (100+ pages) | 100-500ms | Depends on complexity |
| Save PDF | < 20ms | Small files |
| Merge 3 PDFs | 100-200ms | Depends on page count |
| Split 5 pages | 50-100ms | Creates temp files |
| Encrypt (AES-256) | 10-30ms | First encryption |
| Extract text (per page) | 1-5ms | Empty pages faster |
| Convert to image (150 DPI) | 200-300ms per page | CPU intensive |
| Convert to image (72 DPI) | 50-100ms per page | Faster at lower DPI |
| OCR (per page) | 500-2000ms | Depends on Tesseract |

#### 4. Identify Bottlenecks
Look for operations that exceed expected times:
```
2025-11-14 02:05:37.770 - PDF to Images: 2 pages - 150 DPI - 480ms
```
240ms per page is within normal range for 150 DPI.

```
2025-11-14 02:05:36.921 - PDF Merge: 3 files -> 6 pages - 155ms
```
~50ms per file is normal for merge operations.

---

## 🔧 Troubleshooting

### Issue: Tests Fail with "BUILD FAILURE"

#### Check 1: Verify Java Version
```bash
java -version
# Should show Java 17 or higher
```

#### Check 2: Clean and Rebuild
```bash
mvn clean install
mvn test
```

#### Check 3: Check Dependencies
```bash
mvn dependency:tree
# Verify all dependencies are resolved
```

### Issue: Specific Test Fails

#### Example: testMergeNullList fails
```
ERROR - Unexpected exception type thrown, expected: <IllegalArgumentException> but was: <NullPointerException>
```

**Diagnosis**: Method doesn't validate null input before accessing it

**Solution**: Add null check before accessing object properties:
```java
if (files == null) {
    throw new IllegalArgumentException("File list cannot be null");
}
```

### Issue: Logging Not Working

#### Check 1: Verify Log Directory Exists
```bash
dir C:\Users\<YourUsername>\.pdfstudio\logs
```

#### Check 2: Check logback.xml Configuration
- File location: `src/main/resources/logback.xml`
- Verify appenders are configured
- Check file paths match your OS

#### Check 3: Check Log Level
Make sure logger level allows your messages:
```xml
<logger name="app.services.pdf" level="DEBUG"/>
```

### Issue: Performance Issues

#### Symptom: Operations take much longer than expected

**Check 1**: Review performance log for timing
```bash
type pdf-super-studio-performance.log | findstr /C:"PDF to Images"
```

**Check 2**: Check for disk I/O issues
- Large files may take longer
- Network drives are slower
- SSD vs HDD differences

**Check 3**: Check system resources
- CPU usage
- Memory available
- Disk space

### Issue: OCR Returns Stub Results

#### Symptom: OCR text contains "[Stub OCR Result]"

**Cause**: Tesseract OCR is not installed

**Solution**:
1. **Windows**: Download from https://github.com/UB-Mannheim/tesseract/wiki
2. **macOS**: `brew install tesseract`
3. **Linux**: `sudo apt-get install tesseract-ocr`
4. **Verify**: Restart application and check logs for "Tesseract OCR initialized successfully"

---

## 📝 Best Practices

### 1. Run Tests Before Committing Code
```bash
mvn clean test
```
Ensure all 46 tests pass before pushing changes.

### 2. Check Logs After Errors
Don't just rely on console output - check log files for full context.

### 3. Monitor Performance Regularly
Review performance log weekly to detect degradation early.

### 4. Keep Error Logs for Debugging
Error logs are kept for 90 days - use them to track recurring issues.

### 5. Add Logging to New Features
Follow the existing logging patterns:
- DEBUG for method entry/exit
- INFO for successful operations
- WARN for non-critical issues
- ERROR for failures with stack traces

---

## 📞 Getting Help

### Check Documentation
1. README.md - Project overview
2. TESTING_GUIDE.md - This file
3. JavaDoc comments in code

### Check Logs
1. Error log for exceptions
2. Main log for operation flow
3. Performance log for timing issues

### Run Specific Tests
Isolate the problem by running individual tests:
```bash
mvn test -Dtest=PDFServiceImplTest#testLoadSuccess
```

### Enable Verbose Logging
Temporarily increase log level to TRACE for maximum detail:
```xml
<logger name="app.services.pdf" level="TRACE"/>
```

---

## 🎯 Summary

- **All 46 tests passing** (23 PDF + 12 AI + 11 OCR)
- **4 log files** for different purposes
- **Performance tracking** for all major operations
- **Comprehensive error logging** with stack traces
- **Package-specific log levels** for targeted debugging
- **30-90 day log retention** for historical analysis
- **Async logging** for minimal performance impact

The logging and testing infrastructure provides comprehensive visibility into application behavior, making it easy to detect, diagnose, and fix errors quickly.
