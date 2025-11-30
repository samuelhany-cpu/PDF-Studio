@echo off
REM ========================================
REM PDF Super Studio Pro AI - Build Script
REM ========================================

echo.
echo ============================================
echo  PDF Super Studio Pro AI - Build Script
echo ============================================
echo.

REM Check if Maven is installed
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Maven is not installed or not in PATH
    echo Please install Maven from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

REM Check if Java is installed
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed or not in PATH
    echo Please install JDK 21 from: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

REM Check Java version
echo [INFO] Checking Java version...
java -version 2>&1 | find "version" | find "21" >nul
if %errorlevel% neq 0 (
    echo [WARNING] Java 21 is recommended. You may encounter issues with other versions.
)

echo.
echo What would you like to do?
echo.
echo 1. Build the project (mvn clean package)
echo 2. Run the application (mvn javafx:run)
echo 3. Build and Run
echo 4. Run tests (mvn test)
echo 5. Clean project (mvn clean)
echo 6. Create Windows installer (requires jpackage)
echo 7. Exit
echo.
set /p choice="Enter your choice (1-7): "

if "%choice%"=="1" goto build
if "%choice%"=="2" goto run
if "%choice%"=="3" goto buildrun
if "%choice%"=="4" goto test
if "%choice%"=="5" goto clean
if "%choice%"=="6" goto installer
if "%choice%"=="7" goto end
goto menu

:build
echo.
echo ========================================
echo  Building project...
echo ========================================
echo.
call mvn clean package
if %errorlevel% neq 0 (
    echo [ERROR] Build failed!
    pause
    exit /b 1
)
echo.
echo [SUCCESS] Build completed successfully!
echo JAR file: target\pdf-super-studio-pro-ai-1.0.0.jar
pause
goto menu

:run
echo.
echo ========================================
echo  Running application...
echo ========================================
echo.
call mvn javafx:run
pause
goto menu

:buildrun
echo.
echo ========================================
echo  Building and running...
echo ========================================
echo.
call mvn clean package
if %errorlevel% neq 0 (
    echo [ERROR] Build failed!
    pause
    exit /b 1
)
echo.
echo [SUCCESS] Build completed!
echo.
echo Starting application...
call mvn javafx:run
pause
goto menu

:test
echo.
echo ========================================
echo  Running tests...
echo ========================================
echo.
call mvn test
pause
goto menu

:clean
echo.
echo ========================================
echo  Cleaning project...
echo ========================================
echo.
call mvn clean
echo [SUCCESS] Project cleaned!
pause
goto menu

:installer
echo.
echo ========================================
echo  Creating Windows Installer
echo ========================================
echo.
echo [INFO] First, building the project...
call mvn clean package
if %errorlevel% neq 0 (
    echo [ERROR] Build failed!
    pause
    exit /b 1
)

echo.
echo [INFO] Checking for jpackage...
where jpackage >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] jpackage not found!
    echo jpackage is included in JDK 14+ 
    echo Make sure you're using JDK 21 and it's in your PATH
    pause
    exit /b 1
)

echo.
echo [INFO] Creating Windows installer...
echo This may take several minutes...
echo.

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

if %errorlevel% neq 0 (
    echo [ERROR] Installer creation failed!
    pause
    exit /b 1
)

echo.
echo [SUCCESS] Installer created successfully!
echo Look for "PDF Super Studio Pro AI-1.0.0.exe" in the current directory
pause
goto menu

:end
echo.
echo Goodbye!
exit /b 0

:menu
echo.
set /p again="Press ENTER to return to menu or type 'exit' to quit: "
if /i "%again%"=="exit" goto end
cls
goto :eof
