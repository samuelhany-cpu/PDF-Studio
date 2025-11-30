# PDF Super Studio Pro AI

**Enterprise-Grade PDF Manipulation and AI-Powered Analysis Tool**

A professional desktop application for comprehensive PDF operations with integrated offline AI features, built with JavaFX 21, Apache PDFBox, Tesseract OCR, and ONNX Runtime.

## ✨ Features

### 📄 PDF Operations
- Open, save, and edit PDF documents
- Merge multiple PDF files
- Split PDF into separate files
- Compress and optimize PDFs
- Encrypt/decrypt with password protection
- Convert PDF to images (PNG, JPEG, etc.)
- Convert images to PDF
- Extract text content
- Redact sensitive information

### 🤖 AI-Powered Features (Offline)
- **Document Summarization**: Generate concise summaries using local LLM
- **AI Chat**: Ask questions about PDF content
- **Entity Extraction**: Identify people, organizations, locations, dates
- **Translation**: Translate selected text to multiple languages
- **Insights Generation**: Extract key points and structure
- **Sensitive Content Detection**: Identify PII and confidential data
- **Table Extraction**: Detect and extract tabular data
- **Structure Detection**: Identify headings, paragraphs, sections

### 🔍 OCR Features
- Offline OCR using Tesseract
- Support for 15+ languages
- Confidence scoring
- Batch processing

### 🎨 UI Features
- Microsoft Fluent Design style
- Dark and Light themes
- Ribbon-based interface (Adobe Acrobat Pro style)
- Dual sidebars for navigation and AI features
- Responsive layout
- Status bar with progress indicators

## 🛠️ Technology Stack

- **Language**: Java 21
- **UI Framework**: JavaFX 21
- **Build Tool**: Maven
- **PDF Library**: Apache PDFBox 3.0.1
- **OCR Engine**: Tesseract (via Tess4j)
- **AI Runtime**: ONNX Runtime 1.16.3
- **Logging**: SLF4J + Logback

## 📋 Prerequisites

### Required
- Java Development Kit (JDK) 21 or higher
- Maven 3.8+
- Git

### Optional (for full functionality)
- **Tesseract OCR** (for OCR features)
  - Windows: Download from [UB-Mannheim/tesseract](https://github.com/UB-Mannheim/tesseract/wiki)
  - macOS: `brew install tesseract`
  - Linux: `sudo apt-get install tesseract-ocr`

- **LLaMA Model** (for AI features)
  - Download LLaMA 3.1 8B or 4B quantized GGUF model
  - Convert to ONNX format using [llama.cpp](https://github.com/ggerganov/llama.cpp)
  - Place in `models/` directory

## 🚀 Quick Start

### 1. Clone the Repository
```cmd
git clone <repository-url>
cd pdf-super-studio-pro-ai
```

### 2. Build the Project
```cmd
mvn clean package
```

### 3. Run the Application
```cmd
mvn javafx:run
```

Or run the JAR directly:
```cmd
java -jar target\pdf-super-studio-pro-ai-1.0.0.jar
```

## 📦 Building Distribution Packages

### Generate Icons (One-time setup)
The project includes Logo.svg. Convert it to PNG icons:

```cmd
REM Using ImageMagick or Inkscape
inkscape src\main\resources\logo\Logo.svg --export-filename=src\main\resources\icons\icon512.png --export-width=512
inkscape src\main\resources\logo\Logo.svg --export-filename=src\main\resources\icons\icon256.png --export-width=256
inkscape src\main\resources\logo\Logo.svg --export-filename=src\main\resources\icons\icon128.png --export-width=128
```

### Windows (.exe + Installer)

```cmd
REM Build the application
mvn clean package

REM Create Windows executable using jpackage
jpackage ^
  --input target ^
  --name "PDF Super Studio Pro AI" ^
  --main-jar pdf-super-studio-pro-ai-1.0.0.jar ^
  --main-class app.Main ^
  --type exe ^
  --icon src\main\resources\icons\icon512.png ^
  --app-version 1.0.0 ^
  --vendor "PDF Studio" ^
  --description "Enterprise PDF Manipulation and AI Analysis Tool" ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut
```

### macOS (.app + DMG)

```bash
# Build the application
mvn clean package

# Convert PNG to ICNS for macOS
# Use online converter or: png2icns icon512.icns icon512.png

# Create macOS application bundle
jpackage \
  --input target \
  --name "PDF Super Studio Pro AI" \
  --main-jar pdf-super-studio-pro-ai-1.0.0.jar \
  --main-class app.Main \
  --type dmg \
  --icon src/main/resources/icons/icon512.icns \
  --app-version 1.0.0 \
  --vendor "PDF Studio" \
  --description "Enterprise PDF Manipulation and AI Analysis Tool" \
  --mac-package-name "PDFSuperStudioProAI"
```

### Linux (AppImage / DEB / RPM)

```bash
# Build the application
mvn clean package

# Create Linux AppImage
jpackage \
  --input target \
  --name "PDF Super Studio Pro AI" \
  --main-jar pdf-super-studio-pro-ai-1.0.0.jar \
  --main-class app.Main \
  --type app-image \
  --icon src/main/resources/icons/icon512.png \
  --app-version 1.0.0 \
  --vendor "PDF Studio" \
  --description "Enterprise PDF Manipulation and AI Analysis Tool"

# Or create DEB package
jpackage \
  --input target \
  --name "pdf-super-studio-pro-ai" \
  --main-jar pdf-super-studio-pro-ai-1.0.0.jar \
  --main-class app.Main \
  --type deb \
  --icon src/main/resources/icons/icon512.png \
  --app-version 1.0.0 \
  --linux-package-name "pdf-super-studio-pro-ai" \
  --linux-app-category "Office" \
  --linux-shortcut
```

## 🗂️ Project Structure

```
pdf-super-studio-pro-ai/
│
├── pom.xml                          # Maven configuration
├── README.md                        # This file
│
├── src/main/
│   ├── java/app/
│   │   ├── Main.java               # Application entry point
│   │   ├── App.java                # JavaFX Application class
│   │   │
│   │   ├── controllers/            # JavaFX Controllers
│   │   │   └── MainController.java
│   │   │
│   │   ├── model/                  # Data models
│   │   │   ├── PDFDocument.java
│   │   │   ├── AIResponse.java
│   │   │   ├── OCRResult.java
│   │   │   └── AppSettings.java
│   │   │
│   │   ├── services/               # Business logic
│   │   │   ├── pdf/
│   │   │   │   ├── PDFService.java
│   │   │   │   └── PDFServiceImpl.java
│   │   │   ├── ai/
│   │   │   │   ├── AIService.java
│   │   │   │   └── AIServiceImpl.java
│   │   │   └── ocr/
│   │   │       ├── OCRService.java
│   │   │       └── OCRServiceImpl.java
│   │   │
│   │   └── utils/                  # Utilities
│   │       ├── ThemeManager.java
│   │       ├── ConfigManager.java
│   │       └── FileUtils.java
│   │
│   └── resources/
│       ├── fxml/                   # UI layouts
│       │   └── MainWindow.fxml
│       ├── themes/                 # CSS themes
│       │   ├── dark-theme.css
│       │   └── light-theme.css
│       ├── icons/                  # Application icons
│       │   ├── icon512.png
│       │   ├── icon256.png
│       │   └── icon128.png
│       └── logo/                   # Logo assets
│           └── Logo.svg
│
└── docs/
    └── TEST_SCENARIOS.md           # Testing documentation
```

## 🧪 Testing

See [TEST_SCENARIOS.md](docs/TEST_SCENARIOS.md) for comprehensive testing scenarios including:
- PDF operations (merge, split, compress, convert)
- Security features (encrypt, decrypt, redact)
- AI features (summarize, chat, extract, translate)
- OCR features (multi-language, confidence scoring)
- UI features (themes, responsiveness, high DPI)

## ⚙️ Configuration

Configuration is stored in:
- **Windows**: `C:\Users\<username>\.pdfstudio\config.json`
- **macOS**: `/Users/<username>/.pdfstudio/config.json`
- **Linux**: `/home/<username>/.pdfstudio/config.json`

### Configuration Options
```json
{
  "darkMode": true,
  "defaultOCRLanguage": "English",
  "aiModelPath": "",
  "autoSave": false,
  "autoSaveIntervalMinutes": 5,
  "defaultZoom": 1.0,
  "showLeftSidebar": true,
  "showRightSidebar": true,
  "showStatusBar": true
}
```

## 🎯 AI Model Setup (Optional)

**Good News:** If you already have models in a `Models` folder, just copy them:

### Quick Setup (Automated - Easiest!)
```cmd
REM Run the automated setup script
quick-setup-models.bat
```
This will automatically copy all `.onnx` models from `F:\Models` to the project.

### Manual Setup
```cmd
mkdir models
copy F:\Models\*.onnx models\
```

### Configure in Application
1. Launch PDF Super Studio Pro AI
2. Settings → Preferences → AI Model
3. Browse to: `F:\PDF Studio\models\your-model.onnx`
4. Click OK and restart

That's it! Your AI features are now ready to use offline.

**Interactive Setup:** Run `setup-ai-models.bat` for a menu-driven setup experience.

For detailed setup or converting other formats, see [AI_MODEL_SETUP.md](docs/AI_MODEL_SETUP.md)

## 🐛 Troubleshooting

### JavaFX Not Found
Ensure JavaFX is properly configured in Maven and your JDK supports JavaFX 21.

### Tesseract Not Found
Install Tesseract OCR and ensure it's in your system PATH. The application will work without it but OCR features will show stub data.

### ONNX Runtime Issues
Check that ONNX Runtime is compatible with your system architecture (x64/ARM). AI features will work in stub mode without a model.

### High DPI Scaling Issues
Add JVM arguments:
```cmd
java -Dglass.win.uiScale=150% -jar target\pdf-super-studio-pro-ai-1.0.0.jar
```

## 📄 License

Copyright © 2025 PDF Super Studio Pro AI. All rights reserved.

## 🤝 Contributing

This is a demonstration project. For enterprise deployment, ensure proper licensing for all dependencies.

## 📞 Support

For issues and feature requests, please file a GitHub issue.

---

**Built with ❤️ using Java 21 and JavaFX**
