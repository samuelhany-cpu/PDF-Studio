# AI Model Setup Guide

## Current Status

✅ **ONNX Runtime Integration**: Complete  
✅ **Model Loading**: Working  
✅ **Inference Pipeline**: Functional  
⚠️ **Tokenizer**: Using simplified stub (needs proper BERT tokenizer)  

## The Tokenizer Issue

Your BERT model is running successfully, but the output shows `[TOKEN_XXX]` placeholders because the `SimpleTokenizer` class doesn't have the actual BERT vocabulary.

### What's Happening:
1. ✅ Model loads correctly (BERT with 30,522 vocab size)
2. ✅ Input is tokenized (using hash-based approximation)
3. ✅ Model generates output tokens
4. ❌ Output tokens can't be decoded to text (no vocabulary mapping)

### Current Fallback:
The application now automatically falls back to **enhanced stub mode** when it detects token placeholders, so you'll still get useful summaries and chat responses using the rule-based system.

## How to Fix (Production Setup)

To get actual AI-generated responses, you need a proper BERT tokenizer:

### Option 1: Use Hugging Face Tokenizers (Recommended)

1. **Add dependency to `pom.xml`**:
```xml
<dependency>
    <groupId>ai.djl.huggingface</groupId>
    <artifactId>tokenizers</artifactId>
    <version>0.26.0</version>
</dependency>
```

2. **Download tokenizer files** for your BERT model:
   - `vocab.txt` - BERT vocabulary (30,522 tokens)
   - `tokenizer.json` - Tokenizer configuration

3. **Replace `SimpleTokenizer`** with:
```java
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;

// In LLMModelManager constructor:
this.tokenizer = HuggingFaceTokenizer.newInstance(
    Paths.get("models/tokenizer.json")
);
```

### Option 2: Use SentencePiece (Alternative)

For models like LLaMA or custom BERT variants:

1. **Add dependency**:
```xml
<dependency>
    <groupId>com.github.google</groupId>
    <artifactId>sentencepiece</artifactId>
    <version>0.1.0</version>
</dependency>
```

2. **Load SentencePiece model**:
```java
import com.google.sentencepiece.SentencePieceProcessor;

SentencePieceProcessor processor = new SentencePieceProcessor();
processor.loadFromFile("models/sentencepiece.model");
```

### Option 3: Manual Vocabulary Loading

If you only have `vocab.txt`:

```java
private void loadVocabulary(String vocabPath) throws IOException {
    List<String> lines = Files.readAllLines(Paths.get(vocabPath));
    for (int i = 0; i < lines.size(); i++) {
        String word = lines.get(i).trim();
        vocab.put(word, (long) i);
        reverseVocab.put((long) i, word);
    }
}
```

## Model Files You Need

For full AI functionality, place these in `models/` directory:

1. **model.onnx** ✅ (You have this)
2. **vocab.txt** ❌ (BERT vocabulary - 30,522 lines)
3. **tokenizer.json** ❌ (Optional, for Hugging Face tokenizers)

## Where to Get Tokenizer Files

### For BERT Base:
```bash
# Download from Hugging Face
wget https://huggingface.co/bert-base-uncased/resolve/main/vocab.txt
wget https://huggingface.co/bert-base-uncased/resolve/main/tokenizer.json
```

### For Your Specific Model:
If your ONNX model came from a specific Hugging Face model, download from that model's page:
```
https://huggingface.co/[username]/[model-name]/tree/main
```

## Testing the Fix

After adding proper tokenizer:

1. **Restart the application**:
   ```cmd
   mvn clean javafx:run
   ```

2. **Check logs** for:
   ```
   ✅ AI Service initialized with Generic model
   ```

3. **Test AI Summary** - Should see actual generated text instead of token placeholders

4. **Test AI Chat** - Should get contextual responses

## Current Workaround

Until you add the proper tokenizer, the application uses **Enhanced Stub Mode**:

- ✅ Generates useful summaries with statistics and keywords
- ✅ Provides contextual chat responses  
- ✅ Shows document structure and insights
- ❌ Not using the actual AI model (rule-based instead)

## Questions?

The model is loaded and working - you just need the vocabulary file to decode its output. Once you add `vocab.txt`, you'll get real AI-generated responses!

---

**Next Steps:**
1. Find the `vocab.txt` file that matches your BERT model
2. Place it in `models/vocab.txt`
3. Update `LLMModelManager` to load it (see Option 3 above)
4. Rebuild and test
