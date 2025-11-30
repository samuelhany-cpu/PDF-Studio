@echo off
REM Quick AI Model Setup - One Command
REM Copies all ONNX models from F:\PDF Studio\Models to this project folder

echo Copying AI models...
set "SOURCE=F:\PDF Studio\Models"
set "DEST=%~dp0Models"

if /i "%SOURCE%"=="%DEST%" (
    echo [INFO] Script is already in the Models folder. No copy needed.
    echo.
    dir /b "%DEST%\*.onnx"
    goto :end
)

mkdir "%DEST%" 2>nul
copy "%SOURCE%\*.onnx" "%DEST%" /Y
    echo [ERROR] Copy failed. Check if F:\PDF Studio\Models contains .onnx files
)

:end
pause
    echo.
    echo Next: Launch app -> Settings -> AI Model -> Browse to:
    echo %~dp0Models
    echo.
    dir /b "%~dp0Models\*.onnx"
) else (
    echo [ERROR] Copy failed. Check if F:\PDF Studio\Models contains .onnx files
)

pause
