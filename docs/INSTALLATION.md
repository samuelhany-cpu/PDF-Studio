# Installation Guide - PDF Super Studio Pro AI

Complete step-by-step installation guide for Windows, macOS, and Linux.

---

## 🪟 Windows Installation

### Prerequisites

1. **Install Java Development Kit (JDK) 21**
   - Download: https://www.oracle.com/java/technologies/downloads/#java21
   - Choose "Windows x64 Installer"
   - Run installer, accept defaults
   - Verify installation:
     ```cmd
     java -version
     ```
     Should show: `java version "21..."`

2. **Install Apache Maven**
   - Download: https://maven.apache.org/download.cgi
   - Extract ZIP to `C:\Program Files\Apache\maven`
   - Add to PATH:
     - Right-click "This PC" → Properties
     - Advanced system settings → Environment Variables
     - System variables → Path → Edit → New
     - Add: `C:\Program Files\Apache\maven\bin`
   - Verify:
     ```cmd
     mvn -version
     ```

3. **Install Git (Optional)**
   - Download: https://git-scm.com/download/win
   - Run installer with default options

### Build from Source

1. **Open Command Prompt**
   ```cmd
   cd "F:\PDF Studio"
   ```

2. **Run Build Script**
   ```cmd
   build.bat
   ```
   Select option 3 (Build and Run)

   **OR manually**:
   ```cmd
   mvn clean package
   mvn javafx:run
   ```

### Create Windows Installer

1. **Run Build Script**
   ```cmd
   build.bat
   ```
   Select option 6 (Create Windows Installer)

2. **Install Application**
   - Double-click `PDF Super Studio Pro AI-1.0.0.exe`
   - Follow installer wizard
   - Choose installation directory
   - Create desktop shortcut (recommended)
   - Click Install

3. **Run Application**
   - Desktop shortcut: "PDF Super Studio Pro AI"
   - Start Menu: All Apps → PDF Studio → PDF Super Studio Pro AI

---

## 🍎 macOS Installation

### Prerequisites

1. **Install Homebrew** (if not already installed)
   ```bash
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```

2. **Install Java 21**
   ```bash
   brew install openjdk@21
   ```

3. **Install Maven**
   ```bash
   brew install maven
   ```

4. **Verify Installations**
   ```bash
   java -version
   mvn -version
   ```

### Build from Source

1. **Open Terminal**
   ```bash
   cd ~/PDFStudio  # or your project location
   ```

2. **Make Build Script Executable**
   ```bash
   chmod +x build.sh
   ```

3. **Run Build Script**
   ```bash
   ./build.sh
   ```
   Select option 3 (Build and Run)

   **OR manually**:
   ```bash
   mvn clean package
   mvn javafx:run
   ```

### Create macOS Installer

1. **Convert Icon to ICNS Format**
   - Use online converter or:
   ```bash
   # Install ImageMagick
   brew install imagemagick
   
   # Convert PNG to ICNS
   mkdir icon.iconset
   sips -z 512 512   src/main/resources/icons/icon512.png --out icon.iconset/icon_512x512.png
   iconutil -c icns icon.iconset -o src/main/resources/icons/icon512.icns
   ```

2. **Run Build Script**
   ```bash
   ./build.sh
   ```
   Select option 6 (Create macOS Installer)

3. **Install Application**
   - Double-click `PDF Super Studio Pro AI-1.0.0.dmg`
   - Drag app to Applications folder
   - Eject DMG

4. **Run Application**
   - Open Finder → Applications
   - Double-click "PDF Super Studio Pro AI"
   - If security warning appears:
     - System Preferences → Security & Privacy
     - Click "Open Anyway"

---

## 🐧 Linux Installation

### Ubuntu/Debian

1. **Install JDK 21**
   ```bash
   sudo apt update
   sudo apt install openjdk-21-jdk
   ```

2. **Install Maven**
   ```bash
   sudo apt install maven
   ```

3. **Verify Installations**
   ```bash
   java -version
   mvn -version
   ```

### Fedora/RHEL

1. **Install JDK 21**
   ```bash
   sudo dnf install java-21-openjdk-devel
   ```

2. **Install Maven**
   ```bash
   sudo dnf install maven
   ```

### Build from Source

1. **Navigate to Project**
   ```bash
   cd ~/PDFStudio  # or your project location
   ```

2. **Make Build Script Executable**
   ```bash
   chmod +x build.sh
   ```

3. **Run Build Script**
   ```bash
   ./build.sh
   ```
   Select option 3 (Build and Run)

### Create Linux Package

#### DEB Package (Ubuntu/Debian)

```bash
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

Install:
```bash
sudo dpkg -i pdf-super-studio-pro-ai_1.0.0_amd64.deb
```

#### RPM Package (Fedora/RHEL)

```bash
jpackage \
  --input target \
  --name "pdf-super-studio-pro-ai" \
  --main-jar pdf-super-studio-pro-ai-1.0.0.jar \
  --main-class app.Main \
  --type rpm \
  --icon src/main/resources/icons/icon512.png \
  --app-version 1.0.0 \
  --linux-package-name "pdf-super-studio-pro-ai" \
  --linux-app-category "Office" \
  --linux-shortcut
```

Install:
```bash
sudo rpm -i pdf-super-studio-pro-ai-1.0.0.x86_64.rpm
```

---

## 🔧 Post-Installation Setup

### 1. Install Tesseract OCR (Optional, for OCR features)

#### Windows
1. Download: https://github.com/UB-Mannheim/tesseract/wiki
2. Run installer
3. Add to PATH: `C:\Program Files\Tesseract-OCR`

#### macOS
```bash
brew install tesseract
brew install tesseract-lang  # Additional languages
```

#### Linux
```bash
sudo apt install tesseract-ocr
sudo apt install tesseract-ocr-all  # All languages
```

### 2. Configure AI Model (Optional, for AI features)

**If you already have models downloaded:**

1. **Copy models to project** (Windows):
   ```cmd
   mkdir "F:\PDF Studio\models"
   copy "F:\Models\*.onnx" "F:\PDF Studio\models\"
   ```

   **macOS/Linux**:
   ```bash
   mkdir -p ~/PDFStudio/models
   cp ~/Models/*.onnx ~/PDFStudio/models/
   ```

2. **Configure in application:**
   - Launch PDF Super Studio Pro AI
   - Click Settings → Preferences
   - Navigate to AI Model section
   - Click Browse
   - Select your model: `F:\PDF Studio\models\your-model.onnx`
   - Click OK
   - Restart application

**Don't have models yet?** See detailed guide: `docs/AI_MODEL_SETUP.md`

### 3. First Run Configuration

On first launch:
1. Choose theme (Dark/Light)
2. Select default OCR language
3. Configure auto-save preferences
4. Set default zoom level

Configuration stored in:
- **Windows**: `C:\Users\<username>\.pdfstudio\config.json`
- **macOS**: `/Users/<username>/.pdfstudio/config.json`
- **Linux**: `/home/<username>/.pdfstudio/config.json`

---

## 🧪 Verify Installation

### Test Basic Functionality

1. **Open a PDF**
   - File → Open
   - Select any PDF file
   - Verify it displays correctly

2. **Test Theme Toggle**
   - View → Dark Mode (uncheck)
   - Should switch to light theme

3. **Test OCR** (if Tesseract installed)
   - Open a scanned PDF
   - OCR tab → Run OCR
   - Should extract text

4. **Test AI Features** (if model configured)
   - AI Summary tab → Generate Summary
   - Should produce summary

---

## 🐛 Troubleshooting

### Application Won't Start

**Symptom**: Double-click does nothing or shows error

**Solutions**:
1. Verify Java 21 is installed: `java -version`
2. Check logs:
   - Windows: `%USERPROFILE%\.pdfstudio\logs\application.log`
   - macOS/Linux: `~/.pdfstudio/logs/application.log`
3. Run from command line to see errors:
   ```cmd
   java -jar target\pdf-super-studio-pro-ai-1.0.0.jar
   ```

### JavaFX Not Found

**Symptom**: "Error: JavaFX runtime components are missing"

**Solution**: Use Maven to run (it includes JavaFX):
```cmd
mvn javafx:run
```

### Memory Errors

**Symptom**: "OutOfMemoryError" when opening large PDFs

**Solution**: Increase heap size:
```cmd
java -Xmx4G -jar pdf-super-studio-pro-ai-1.0.0.jar
```

### Tesseract Not Found

**Symptom**: OCR shows "Tesseract not available"

**Solution**: 
1. Install Tesseract (see above)
2. Verify: `tesseract --version`
3. Restart application

### High DPI Issues (Windows)

**Symptom**: Blurry text on high-resolution displays

**Solution**:
1. Right-click application → Properties
2. Compatibility → Change high DPI settings
3. Check "Override high DPI scaling"
4. Select "System (Enhanced)"

---

## 📦 Uninstallation

### Windows
- Control Panel → Programs → Uninstall a program
- Select "PDF Super Studio Pro AI" → Uninstall

### macOS
- Drag app from Applications to Trash
- Empty Trash

### Linux (DEB)
```bash
sudo apt remove pdf-super-studio-pro-ai
```

### Linux (RPM)
```bash
sudo rpm -e pdf-super-studio-pro-ai
```

### Remove Configuration Files
Delete configuration directory:
- Windows: `C:\Users\<username>\.pdfstudio`
- macOS: `/Users/<username>/.pdfstudio`
- Linux: `/home/<username>/.pdfstudio`

---

## 📞 Support

### Getting Help
1. Check README.md for common issues
2. Review docs/QUICK_START.md
3. Check application logs
4. Search error messages online

### Reporting Issues
When reporting issues, include:
- Operating system and version
- Java version (`java -version`)
- Error message or behavior
- Steps to reproduce
- Log files (`.pdfstudio/logs/application.log`)

---

## 🎉 You're All Set!

PDF Super Studio Pro AI is now installed and ready to use.

**Quick Start**: See `docs/QUICK_START.md`
**Full Documentation**: See `README.md`

Enjoy your professional PDF manipulation tool! 🚀
