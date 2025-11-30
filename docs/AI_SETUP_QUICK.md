# ✅ AI Model Setup - Simplified for Pre-Downloaded Models

## What's New

The AI model setup has been **significantly simplified** because your models are already downloaded in the `Models` folder!

---

## 🎯 Three Easy Ways to Set Up AI Models

### Method 1: One-Click Setup ⚡ (Fastest - 5 Seconds!)
```cmd
cd "F:\PDF Studio"
quick-setup-models.bat
```
**What it does:**
- Automatically copies all `.onnx` files from `F:\Models`
- Creates `models` folder if needed
- Shows you what was copied
- Done!

---

### Method 2: Interactive Menu Setup 🎛️ (Most Flexible)
```cmd
cd "F:\PDF Studio"
setup-ai-models.bat
```
**Features:**
- Menu-driven interface
- Choose which models to copy
- Supports both ONNX and GGUF formats
- Select specific files or copy all
- Automatic path detection

---

### Method 3: Manual Copy 📋 (Full Control)
```cmd
mkdir "F:\PDF Studio\models"
copy "F:\Models\*.onnx" "F:\PDF Studio\models\"
```

---

## 📝 After Copying Models

**Configure in the application:**

1. **Launch PDF Super Studio Pro AI**
   ```cmd
   mvn javafx:run
   ```
   Or run `build.bat` → Option 2

2. **Open Settings**
   - Click **Settings** ribbon tab
   - Or Menu: **File → Preferences**

3. **Browse to Model**
   - Navigate to **AI Model** section
   - Click **Browse**
   - Select: `F:\PDF Studio\models\your-model-name.onnx`
   - Click **OK**

4. **Restart Application**
   - Close and relaunch
   - AI features are now enabled!

---

## 🧪 Test AI Features

After setup, test these features:

### 1. AI Summary
- Open any PDF
- Right sidebar → **AI Summary** tab
- Click **Generate Summary**
- Wait 10-30 seconds
- Read AI-generated summary

### 2. AI Chat
- Open a PDF
- Right sidebar → **AI Chat** tab
- Type: "What is this document about?"
- Click **Send**
- Get AI response based on PDF content

### 3. Entity Extraction
- Open a PDF
- Right sidebar → **Insights** tab
- Click **Extract Entities**
- See names, organizations, locations, dates

---

## 📂 Model Files Overview

### Supported Formats

| Format | Status | Notes |
|--------|--------|-------|
| `.onnx` | ✅ Ready to use | Directly compatible with ONNX Runtime |
| `.gguf` | ⚠️ Needs conversion | See AI_MODEL_SETUP.md for conversion |

### Recommended Models

From your `F:\Models` folder, look for:

**LLaMA Models:**
- `llama-3.1-8b-instruct.onnx` (Best quality, 8GB RAM)
- `llama-3.1-4b-instruct.onnx` (Faster, 4GB RAM)

**Phi-3 Models:**
- `phi-3-mini.onnx` (Lightweight, 2GB RAM)

**Other Compatible:**
- Any ONNX model with text generation capability

---

## 🔍 Verify Setup

### Check Models Were Copied
```cmd
dir "F:\PDF Studio\models"
```
You should see `.onnx` files listed.

### Check Application Logs
After configuring and restarting:
```cmd
type "%USERPROFILE%\.pdfstudio\logs\application.log" | find "AI model"
```

Look for:
```
INFO  app.services.ai.AIServiceImpl - AI model loaded successfully from: F:\PDF Studio\models\...
```

### Quick Test
1. Open any PDF
2. Generate AI Summary
3. If you see actual summary (not stub message), it's working! ✅

---

## 🛠️ Troubleshooting

### Models Not Copying
**Problem:** `quick-setup-models.bat` shows error

**Solutions:**
1. Check if `F:\Models` exists:
   ```cmd
   dir F:\Models
   ```
2. Verify ONNX files exist:
   ```cmd
   dir F:\Models\*.onnx
   ```
3. Try interactive setup:
   ```cmd
   setup-ai-models.bat
   ```
   And manually enter your Models folder path

### AI Features Show Stub Responses
**Problem:** Summary says "[Full AI summary requires local LLM model]"

**Cause:** Model not configured or not loaded

**Solutions:**
1. Check model file path in Settings
2. Verify model file exists:
   ```cmd
   dir "F:\PDF Studio\models\*.onnx"
   ```
3. Check logs for errors:
   ```cmd
   type "%USERPROFILE%\.pdfstudio\logs\application.log"
   ```
4. Ensure you **restarted** the application after configuration

### Out of Memory
**Problem:** Application crashes when using AI

**Solution:** Increase Java heap size:
```cmd
java -Xmx16G -jar target\pdf-super-studio-pro-ai-1.0.0.jar
```

Or use smaller model:
- Switch from 8B to 4B model
- Use Phi-3 Mini instead of LLaMA

---

## 📊 Model Comparison

From your Models folder, choose based on your needs:

| Model | Size | Speed | Quality | RAM Required |
|-------|------|-------|---------|--------------|
| Phi-3 Mini | 2GB | ⚡⚡⚡ Fast | Good | 8GB |
| LLaMA 4B | 3GB | ⚡⚡ Medium | Very Good | 12GB |
| LLaMA 8B | 5GB | ⚡ Slower | Excellent | 16GB+ |

**Recommendation:** Start with **LLaMA 4B** for best balance of speed and quality.

---

## 🎉 You're All Set!

With your pre-downloaded models, setup takes **less than 1 minute**:

1. ✅ Run `quick-setup-models.bat` (5 seconds)
2. ✅ Configure in app Settings (30 seconds)
3. ✅ Restart application (10 seconds)
4. ✅ Test AI features (works!)

**Your PDF tool now has powerful AI capabilities, 100% offline!** 🚀

---

## 📚 Additional Resources

- **Full AI Guide:** `docs/AI_MODEL_SETUP.md`
- **Quick Start:** `docs/QUICK_START.md`
- **Troubleshooting:** `docs/INSTALLATION.md`

---

**Last Updated:** November 14, 2025  
**Version:** 1.0.0  
**Status:** ✅ Simplified for pre-downloaded models
