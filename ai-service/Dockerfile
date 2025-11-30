FROM eclipse-temurin:17-jre-alpine

# Install curl for healthcheck
RUN apk add --no-cache curl

# Set working directory
WORKDIR /app

# Copy JAR file
COPY target/ai-service-1.0.0.jar app.jar

# Create directories for models and llama.cpp
RUN mkdir -p /app/models /app/llama.cpp /app/logs

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:8081/api/ai/health || exit 1

# Run the application
ENTRYPOINT ["java", "-Xmx2g", "-jar", "app.jar"]
