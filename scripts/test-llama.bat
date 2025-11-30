 @echo off
echo ========================================
echo  Testing llama.cpp Installation
echo ========================================
echo.

echo Checking if llama-cli.exe exists...
if exist "llama.cpp\llama-cli.exe" (
    echo ✓ Found: llama.cpp\llama-cli.exe
    echo.
    echo Testing llama-cli version:
    llama.cpp\llama-cli.exe --version
    echo.
    echo ✓ llama.cpp is ready!
) else (
    echo ✗ llama-cli.exe not found!
    echo.
    echo Please download llama.cpp and extract to:
    echo   F:\PDF Studio\llama.cpp\
    echo.
    echo Download from: https://github.com/ggerganov/llama.cpp/releases
)

echo.
echo Checking GGUF model...
if exist "models\Llama-3.2-3B-Instruct-Q6_K_L_2.gguf" (
    echo ✓ Found: models\Llama-3.2-3B-Instruct-Q6_K_L_2.gguf
) else (
    echo ✗ GGUF model not found in models\
)

echo.
pause
