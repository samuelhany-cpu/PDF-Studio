# Quick Start Guide - PDF Super Studio Pro AI

## 🚀 Getting Started in 5 Minutes

### Step 1: Build the Project
```cmd
cd F:\PDF Studio
mvn clean package
```

### Step 2: Run the Application
```cmd
mvn javafx:run
```

### Step 3: Open Your First PDF
1. Click **File → Open** or use the **Open** button in left sidebar
2. Select a PDF file from your computer
3. The PDF will load in the center viewer panel

### Step 4: Try Core Features

#### 📄 Basic PDF Operations
- **Save**: File → Save (Ctrl+S)
- **Merge**: Tools → Merge PDFs → Add files → Merge
- **Split**: Tools → Split PDF → Select ranges → Split
- **Compress**: Tools → Compress PDF → Choose quality → Compress

#### 🤖 AI Features (Right Sidebar)
1. Open the **AI Summary** tab
2. Click **Generate Summary**
3. Wait a few seconds for AI to analyze the document
4. Read the summary in the text area

#### 🔍 OCR Features
1. Open a scanned PDF
2. Navigate to **OCR** tab in right sidebar
3. Select language (e.g., English)
4. Click **Run OCR**
5. View extracted text

#### 🎨 Change Theme
- Click **View → Dark Mode** to toggle between dark and light themes

## 📝 Example Workflows

### Workflow 1: Merge Multiple PDFs
```
1. Tools → Merge PDFs
2. Add file 1: contract.pdf
3. Add file 2: appendix.pdf
4. Add file 3: signatures.pdf
5. Click "Merge"
6. Save as: combined_contract.pdf
```

### Workflow 2: Extract and Summarize
```
1. Open research_paper.pdf
2. AI Summary tab → Generate Summary
3. Read key points
4. AI Chat tab → Ask "What methodology was used?"
5. Get AI response based on document content
```

### Workflow 3: OCR a Scanned Document
```
1. Open scanned_invoice.pdf
2. OCR tab → Select "English"
3. Click "Run OCR"
4. Copy extracted text to clipboard
5. Use text for data entry or analysis
```

## ⚙️ Initial Configuration

### Set Your Preferences
1. Click **Settings** ribbon tab
2. Configure:
   - Default zoom level
   - Auto-save interval
   - Theme preference
   - Default OCR language

### Optional: Configure AI Model
If you have models in your Models folder:
1. Copy models to project:
   ```cmd
   mkdir "F:\PDF Studio\models"
   copy "F:\Models\*.onnx" "F:\PDF Studio\models\"
   ```
2. In application: Settings → AI Model → Browse
3. Select your ONNX model file
4. Restart application

See detailed guide: `docs/AI_MODEL_SETUP.md`

## 🐛 Common Issues

### Issue: JavaFX Not Found
**Solution**: Ensure you're using JDK 21 with JavaFX support
```cmd
java -version  # Should show Java 21
mvn javafx:run # Use Maven plugin
```

### Issue: Tesseract Not Found
**Solution**: Install Tesseract OCR
- Windows: Download from https://github.com/UB-Mannheim/tesseract/wiki
- macOS: `brew install tesseract`
- Linux: `sudo apt-get install tesseract-ocr`

### Issue: PDF Won't Open
**Solution**: Check file permissions and ensure PDF is not corrupted
```cmd
# Test with a simple PDF first
# Create a test PDF using any PDF creator
```

## 📚 Next Steps

1. **Read Full Documentation**: See README.md
2. **Review Test Scenarios**: See docs/TEST_SCENARIOS.md
3. **Explore AI Features**: Try all AI tabs in right sidebar
4. **Customize UI**: Adjust sidebars and theme to your preference
5. **Create Distribution**: Use jpackage commands from README

## 💡 Pro Tips

- **Keyboard Shortcuts**:
  - Ctrl+O: Open PDF
  - Ctrl+S: Save PDF
  - Ctrl+W: Close PDF
  
- **Performance**:
  - Close unused PDFs to free memory
  - Use compression for large PDFs before sharing
  
- **AI Features**:
  - AI works best with text-based PDFs
  - Run OCR first on scanned documents before using AI
  - Summaries work better on structured documents

- **Themes**:
  - Dark mode reduces eye strain in low light
  - Light mode better for printing/screenshots

## 🎯 Quick Command Reference

```cmd
# Build project
mvn clean package

# Run application
mvn javafx:run

# Run tests
mvn test

# Create Windows installer
jpackage --input target --name "PDF Super Studio Pro AI" ...

# View logs
type %USERPROFILE%\.pdfstudio\logs\application.log
```

## 📞 Get Help

- Check console output for errors
- Review logs in `~/.pdfstudio/logs/`
- Ensure all dependencies are installed
- Verify JDK 21 and Maven are properly configured

---

**You're all set! Start exploring PDF Super Studio Pro AI!** 🎉
