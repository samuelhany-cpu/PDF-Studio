@echo off
REM ================================================================
REM PDF Super Studio Pro AI - Quick AI Model Setup
REM Copy pre-downloaded models from your Models folder
REM ================================================================

echo.
echo ============================================
echo  PDF Super Studio Pro AI - AI Model Setup
echo ============================================
echo.

REM Default source folder
set "SOURCE_FOLDER=F:\Models"

REM Check if source folder exists
if not exist "%SOURCE_FOLDER%" (
    echo [INFO] Default Models folder not found at: %SOURCE_FOLDER%
    echo.
    set /p SOURCE_FOLDER="Enter the path to your Models folder: "
)

if not exist "%SOURCE_FOLDER%" (
    echo [ERROR] Models folder not found: %SOURCE_FOLDER%
    echo Please check the path and try again.
    pause
    exit /b 1
)

echo [INFO] Source folder: %SOURCE_FOLDER%
echo.

REM Create models directory in project
set "DEST_FOLDER=F:\PDF Studio\models"

if not exist "%DEST_FOLDER%" (
    echo [INFO] Creating models directory...
    mkdir "%DEST_FOLDER%"
    echo [SUCCESS] Directory created: %DEST_FOLDER%
) else (
    echo [INFO] Models directory already exists: %DEST_FOLDER%
)

echo.
echo Available models in source folder:
echo.
dir /b "%SOURCE_FOLDER%\*.onnx" 2>nul
dir /b "%SOURCE_FOLDER%\*.gguf" 2>nul

if %errorlevel% neq 0 (
    echo [WARNING] No .onnx or .gguf files found in source folder
    echo.
    echo Please ensure you have AI model files (.onnx or .gguf) in:
    echo %SOURCE_FOLDER%
    pause
    exit /b 1
)

echo.
echo What would you like to copy?
echo.
echo 1. Copy all ONNX models (.onnx)
echo 2. Copy all GGUF models (.gguf)
echo 3. Copy all models (both .onnx and .gguf)
echo 4. Select specific model file
echo 5. Exit
echo.
set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" goto copy_onnx
if "%choice%"=="2" goto copy_gguf
if "%choice%"=="3" goto copy_all
if "%choice%"=="4" goto select_file
if "%choice%"=="5" goto end
goto menu

:copy_onnx
echo.
echo [INFO] Copying ONNX models...
copy "%SOURCE_FOLDER%\*.onnx" "%DEST_FOLDER%\" /Y
if %errorlevel% equ 0 (
    echo [SUCCESS] ONNX models copied successfully!
) else (
    echo [ERROR] Failed to copy models
)
goto finish

:copy_gguf
echo.
echo [INFO] Copying GGUF models...
copy "%SOURCE_FOLDER%\*.gguf" "%DEST_FOLDER%\" /Y
if %errorlevel% equ 0 (
    echo [SUCCESS] GGUF models copied successfully!
    echo [NOTE] GGUF models may need conversion to ONNX format
    echo See docs\AI_MODEL_SETUP.md for conversion instructions
) else (
    echo [ERROR] Failed to copy models
)
goto finish

:copy_all
echo.
echo [INFO] Copying all models...
copy "%SOURCE_FOLDER%\*.onnx" "%DEST_FOLDER%\" /Y 2>nul
copy "%SOURCE_FOLDER%\*.gguf" "%DEST_FOLDER%\" /Y 2>nul
echo [SUCCESS] All models copied!
goto finish

:select_file
echo.
echo Available model files:
echo.
set idx=0
for %%f in ("%SOURCE_FOLDER%\*.onnx" "%SOURCE_FOLDER%\*.gguf") do (
    set /a idx+=1
    echo !idx!. %%~nxf
    set "file_!idx!=%%f"
)

if %idx% equ 0 (
    echo [ERROR] No model files found
    pause
    exit /b 1
)

echo.
set /p file_choice="Enter file number to copy: "

if defined file_%file_choice% (
    call set file_to_copy=%%file_%file_choice%%%
    echo [INFO] Copying: !file_to_copy!
    copy "!file_to_copy!" "%DEST_FOLDER%\" /Y
    if %errorlevel% equ 0 (
        echo [SUCCESS] Model copied successfully!
    ) else (
        echo [ERROR] Failed to copy model
    )
) else (
    echo [ERROR] Invalid selection
)
goto finish

:finish
echo.
echo ============================================
echo  Setup Complete!
echo ============================================
echo.
echo Models location: %DEST_FOLDER%
echo.
echo Next steps:
echo 1. Run PDF Super Studio Pro AI
echo 2. Go to Settings → Preferences
echo 3. Navigate to AI Model section
echo 4. Click Browse and select your model
echo 5. Restart the application
echo.
echo Your AI features will now work offline!
echo.

REM List copied models
echo Copied models:
dir /b "%DEST_FOLDER%"

echo.
pause
goto end

:end
exit /b 0
