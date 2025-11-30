# AI Models Directory

This directory is for GGUF model files (.gguf). Models are NOT included in the repository due to their large size (typically 2-8 GB).

## 📥 Download Models

### Recommended Models

1. **LLaMA 3.2 3B (Recommended for most users)**
   - Size: ~2.1 GB (Q6_K_L quantization)
   - Download: [Hugging Face - LLaMA 3.2 3B GGUF](https://huggingface.co/bartowski/Llama-3.2-3B-Instruct-GGUF)
   - File: `Llama-3.2-3B-Instruct-Q6_K_L.gguf`

2. **Phi-3 Mini (Faster, lighter)**
   - Size: ~2.3 GB
   - Download: [Hugging Face - Phi-3 Mini GGUF](https://huggingface.co/microsoft/Phi-3-mini-4k-instruct-gguf)
   - File: `Phi-3-mini-4k-instruct-q4.gguf`

3. **LLaMA 3.1 8B (Better quality, requires more RAM)**
   - Size: ~5 GB
   - Download: [Hugging Face - LLaMA 3.1 8B GGUF](https://huggingface.co/bartowski/Meta-Llama-3.1-8B-Instruct-GGUF)
   - File: `Meta-Llama-3.1-8B-Instruct-Q6_K.gguf`

## 📋 Installation Steps

1. Download your chosen model from Hugging Face
2. Place the `.gguf` file in this directory (`models/`)
3. Update the path in `ai-service/src/main/resources/application.yml`:
   ```yaml
   ai:
     model:
       path: F:/PDF Studio/models/YOUR_MODEL_NAME.gguf
   ```
4. Restart the AI service

## 🔧 Model Requirements

- **RAM**: 4-8 GB available (depending on model size)
- **Storage**: 2-8 GB per model
- **CPU**: Any modern multi-core processor
- **GPU** (optional): NVIDIA GPU with CUDA for faster inference

## 📖 More Information

- See `docs/AI_MODEL_SETUP.md` for detailed setup instructions
- See `docs/MICROSERVICES_SETUP.md` for AI service configuration
- Models are loaded once at startup and kept in memory

## ⚠️ Important Notes

- Do NOT commit model files to Git (they're in .gitignore)
- Quantized models (Q4, Q6) are recommended for good balance of size/quality
- F16 models are larger but offer better quality
- Q8 is the highest quantization level before F16

## 🆘 Troubleshooting

**Model not found error?**
- Check the file path in `application.yml`
- Ensure the `.gguf` file is in this directory
- Verify the filename matches exactly (case-sensitive)

**Out of memory error?**
- Try a smaller model (Phi-3 Mini or Q4 quantization)
- Close other applications
- Increase JVM heap size in startup scripts

**Slow inference?**
- Consider GPU acceleration with CUDA
- Try a smaller quantization (Q4 instead of Q6)
- Ensure no other heavy processes are running
