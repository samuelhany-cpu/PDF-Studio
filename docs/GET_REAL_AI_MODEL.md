# How to Get a Real AI Text Generation Model

## The Problem
Your current `models/model.onnx` is a BERT model, which **cannot generate text**. BERT is an encoder-only model designed for classification and understanding, not generation.

## The Solution: Get a Text Generation Model

### Option 1: Download Pre-converted ONNX Models (Easiest)

#### Phi-3 Mini (Recommended - 3.8GB, Fast)
```bash
# Download from Hugging Face
# Visit: https://huggingface.co/microsoft/Phi-3-mini-4k-instruct-onnx
# Download these files to F:\PDF Studio\models\:
# - model.onnx
# - tokenizer.json
# - tokenizer_config.json
```

#### TinyLlama (Smallest - 1.1GB, Very Fast)
```bash
# Visit: https://huggingface.co/TinyLlama/TinyLlama-1.1B-Chat-v1.0
# Download and convert (see conversion below)
```

### Option 2: Convert a Hugging Face Model to ONNX

#### Install Required Tools
```bash
pip install optimum[onnxruntime]
pip install transformers torch
```

#### Convert Phi-3 Mini to ONNX
```bash
# Navigate to your PDF Studio directory
cd "F:\PDF Studio"

# Create models directory
mkdir models

# Convert model (this will download ~3.8GB)
optimum-cli export onnx --model microsoft/Phi-3-mini-4k-instruct --task text-generation models/
```

#### Convert TinyLlama to ONNX (Smaller, Faster)
```bash
optimum-cli export onnx --model TinyLlama/TinyLlama-1.1B-Chat-v1.0 --task text-generation models/
```

#### Convert GPT-2 to ONNX (Smallest, Good for Testing)
```bash
optimum-cli export onnx --model gpt2 --task text-generation models/
```

### Option 3: Use the Application in Demo Mode (Current)

The application already works perfectly with enhanced stubs that provide:
- ✅ Document statistics (word count, reading time, etc.)
- ✅ Keyword extraction
- ✅ Content preview
- ✅ Basic analysis

This is what you're seeing now - it's fully functional for demonstration purposes!

## Which Model to Choose?

| Model | Size | Speed | Quality | Best For |
|-------|------|-------|---------|----------|
| **GPT-2** | 500MB | ⚡⚡⚡ Very Fast | ⭐⭐ Basic | Testing, development |
| **TinyLlama 1.1B** | 1.1GB | ⚡⚡⚡ Fast | ⭐⭐⭐ Good | Production, fast response |
| **Phi-3 Mini** | 3.8GB | ⚡⚡ Medium | ⭐⭐⭐⭐ Excellent | Production, high quality |
| **LLaMA 3.1 8B** | 16GB | ⚡ Slow | ⭐⭐⭐⭐⭐ Best | Server, research |

## Step-by-Step: Convert GPT-2 (Quickest Test)

1. **Install Python tools:**
```bash
pip install optimum[onnxruntime] transformers torch
```

2. **Convert GPT-2:**
```bash
cd "F:\PDF Studio"
optimum-cli export onnx --model gpt2 --task text-generation models/
```

3. **Restart your application:**
```bash
mvn javafx:run
```

4. **Load a PDF and click "Generate AI Summary"**
You should now see real AI-generated text instead of stubs!

## Troubleshooting

### "Model loads but still shows stubs"
- Your model might be BERT or another encoder-only model
- Only **decoder** or **encoder-decoder** models can generate text
- Look for: GPT, LLaMA, Phi, T5, BART models

### "Model is too slow"
- Use a smaller model (GPT-2, TinyLlama)
- Reduce max_tokens in the code
- Use GPU acceleration (requires ONNX Runtime GPU build)

### "Out of memory errors"
- Use a smaller model
- Close other applications
- Reduce batch size in LLMModelManager

## Understanding Model Types

### ✅ Text Generation Models (What You Need)
- **GPT-2/3/4** - Autoregressive language models
- **LLaMA/LLaMA 2/3** - Meta's open models
- **Phi-3** - Microsoft's efficient models
- **T5/BART** - Encoder-decoder models
- **TinyLlama** - Small but capable

### ❌ Non-Generation Models (Won't Work)
- **BERT** - Encoder-only, for classification
- **RoBERTa** - BERT variant
- **DistilBERT** - Compressed BERT
- **ALBERT** - Lite BERT
- These models **cannot generate text**!

## Quick Test: Check Your Model Type

Run this Python script to check what model you have:

```python
import onnx

# Load your model
model = onnx.load("F:/PDF Studio/models/model.onnx")

# Print model info
print("Model inputs:")
for input in model.graph.input:
    print(f"  - {input.name}: {input.type}")

print("\nModel outputs:")
for output in model.graph.output:
    print(f"  - {output.name}: {output.type}")

# Check for generation capability
has_decoder = any('decoder' in node.name.lower() for node in model.graph.node)
has_lm_head = any('lm_head' in node.name.lower() or 'cls' in node.name.lower() for node in model.graph.node)

print(f"\nHas decoder: {has_decoder}")
print(f"Has language modeling head: {has_lm_head}")

if not (has_decoder or has_lm_head):
    print("\n❌ This is likely a BERT/encoder model - cannot generate text!")
    print("✅ Download a GPT/LLaMA/Phi model instead")
else:
    print("\n✅ This model should work for text generation")
```

## Next Steps

1. **For quick testing:** Convert GPT-2 (~10 minutes, 500MB)
2. **For production:** Download Phi-3 Mini (~30 minutes, 3.8GB)
3. **Keep using demo mode:** Current stubs are fully functional

The application will automatically detect and use any compatible model you place in `F:\PDF Studio\models\model.onnx`!
