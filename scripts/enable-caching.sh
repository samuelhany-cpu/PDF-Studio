#!/bin/bash
# Re-enable Redis Caching Script
# Run this after Docker containers are started

echo "========================================="
echo "  Re-enabling Redis Caching"
echo "========================================="
echo ""

# Backup original application.yml
if [ ! -f "ai-service/src/main/resources/application.yml.backup-nocache" ]; then
    echo "Creating backup of current application.yml..."
    cp ai-service/src/main/resources/application.yml ai-service/src/main/resources/application.yml.backup-nocache
fi

echo "Step 1: Updating application.yml..."
echo "  Removing: spring.data.redis.enabled: false"
echo "  Removing: autoconfigure.exclude section"
echo ""

# Note: For Windows, this script is for reference
# You'll need to manually edit the file or use PowerShell

echo "Manual steps required:"
echo ""
echo "1. Edit: ai-service/src/main/resources/application.yml"
echo "   Remove these lines:"
echo "     spring:"
echo "       data:"
echo "         redis:"
echo "           enabled: false"
echo "     autoconfigure:"
echo "       exclude:"
echo "         - org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration"
echo ""
echo "2. Edit: ai-service/src/main/java/com/pdfstudio/ai/AIServiceApplication.java"
echo "   Add back: @EnableCaching annotation"
echo ""
echo "3. Edit: ai-service/src/main/java/com/pdfstudio/ai/service/AIModelService.java"
echo "   Add back: @Cacheable annotation to generateText() method"
echo ""
echo "4. Edit: ai-service/src/main/java/com/pdfstudio/ai/service/AIOperationsService.java"
echo "   Add back: @Cacheable annotations to summarize() and chat() methods"
echo ""
echo "5. Run: restart-ai-service.bat"
echo ""
echo "See docs/ENABLE_CACHING.md for detailed instructions."
echo ""
