#!/bin/bash
# ========================================
# PDF Super Studio Pro AI - Build Script
# ========================================

# Go to project root (parent of scripts folder)
cd "$(dirname "$0")/.."
echo "Running from: $(pwd)"
echo ""

echo ""
echo "============================================"
echo " PDF Super Studio Pro AI - Build Script"
echo "============================================"
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "[ERROR] Maven is not installed or not in PATH"
    echo "Please install Maven from: https://maven.apache.org/download.cgi"
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "[ERROR] Java is not installed or not in PATH"
    echo "Please install JDK 21"
    exit 1
fi

# Check Java version
echo "[INFO] Checking Java version..."
java -version 2>&1 | grep "version" | grep "21" > /dev/null
if [ $? -ne 0 ]; then
    echo "[WARNING] Java 21 is recommended. You may encounter issues with other versions."
fi

show_menu() {
    echo ""
    echo "What would you like to do?"
    echo ""
    echo "1. Build the project (mvn clean package)"
    echo "2. Run the application (mvn javafx:run)"
    echo "3. Build and Run"
    echo "4. Run tests (mvn test)"
    echo "5. Clean project (mvn clean)"
    echo "6. Create macOS installer (requires jpackage)"
    echo "7. Exit"
    echo ""
    read -p "Enter your choice (1-7): " choice
    
    case $choice in
        1) build ;;
        2) run ;;
        3) buildrun ;;
        4) test ;;
        5) clean ;;
        6) installer ;;
        7) exit 0 ;;
        *) echo "Invalid choice"; show_menu ;;
    esac
}

build() {
    echo ""
    echo "========================================"
    echo " Building project..."
    echo "========================================"
    echo ""
    mvn clean package
    if [ $? -ne 0 ]; then
        echo "[ERROR] Build failed!"
        exit 1
    fi
    echo ""
    echo "[SUCCESS] Build completed successfully!"
    echo "JAR file: target/pdf-super-studio-pro-ai-1.0.0.jar"
    read -p "Press ENTER to continue..."
    show_menu
}

run() {
    echo ""
    echo "========================================"
    echo " Running application..."
    echo "========================================"
    echo ""
    mvn javafx:run
    read -p "Press ENTER to continue..."
    show_menu
}

buildrun() {
    echo ""
    echo "========================================"
    echo " Building and running..."
    echo "========================================"
    echo ""
    mvn clean package
    if [ $? -ne 0 ]; then
        echo "[ERROR] Build failed!"
        exit 1
    fi
    echo ""
    echo "[SUCCESS] Build completed!"
    echo ""
    echo "Starting application..."
    mvn javafx:run
    read -p "Press ENTER to continue..."
    show_menu
}

test() {
    echo ""
    echo "========================================"
    echo " Running tests..."
    echo "========================================"
    echo ""
    mvn test
    read -p "Press ENTER to continue..."
    show_menu
}

clean() {
    echo ""
    echo "========================================"
    echo " Cleaning project..."
    echo "========================================"
    echo ""
    mvn clean
    echo "[SUCCESS] Project cleaned!"
    read -p "Press ENTER to continue..."
    show_menu
}

installer() {
    echo ""
    echo "========================================"
    echo " Creating macOS Installer"
    echo "========================================"
    echo ""
    echo "[INFO] First, building the project..."
    mvn clean package
    if [ $? -ne 0 ]; then
        echo "[ERROR] Build failed!"
        exit 1
    fi
    
    echo ""
    echo "[INFO] Checking for jpackage..."
    if ! command -v jpackage &> /dev/null; then
        echo "[ERROR] jpackage not found!"
        echo "jpackage is included in JDK 14+"
        echo "Make sure you're using JDK 21 and it's in your PATH"
        exit 1
    fi
    
    echo ""
    echo "[INFO] Creating macOS installer..."
    echo "This may take several minutes..."
    echo ""
    
    jpackage \
      --input target \
      --name "PDF Super Studio Pro AI" \
      --main-jar pdf-super-studio-pro-ai-1.0.0.jar \
      --main-class app.Main \
      --type dmg \
      --icon src/main/resources/icons/icon512.png \
      --app-version 1.0.0 \
      --vendor "PDF Studio" \
      --description "Enterprise PDF Manipulation and AI Analysis Tool" \
      --mac-package-name "PDFSuperStudioProAI"
    
    if [ $? -ne 0 ]; then
        echo "[ERROR] Installer creation failed!"
        exit 1
    fi
    
    echo ""
    echo "[SUCCESS] Installer created successfully!"
    echo "Look for 'PDF Super Studio Pro AI-1.0.0.dmg' in the current directory"
    read -p "Press ENTER to continue..."
    show_menu
}

# Start the menu
show_menu
