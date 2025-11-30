package app.aiservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis Cache Configuration
 * Configures different TTLs for different cache types
 * 
 * NOTE: This configuration is DISABLED by default.
 * To enable, set: spring.data.redis.enabled=true in application.yml
 * and ensure Redis is running.
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.data.redis.enabled", havingValue = "true")
public class CacheConfig {
    
    @Value("${ai.cache.summary-ttl:3600}")
    private long summaryTtl;
    
    @Value("${ai.cache.chat-ttl:1800}")
    private long chatTtl;
    
    @Value("${ai.cache.entities-ttl:7200}")
    private long entitiesTtl;
    
    @Value("${ai.cache.translation-ttl:86400}")
    private long translationTtl;
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Default configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(3600))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        
        // Custom configurations per cache
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        cacheConfigurations.put("summaries", defaultConfig
            .entryTtl(Duration.ofSeconds(summaryTtl)));
        
        cacheConfigurations.put("chat", defaultConfig
            .entryTtl(Duration.ofSeconds(chatTtl)));
        
        cacheConfigurations.put("entities", defaultConfig
            .entryTtl(Duration.ofSeconds(entitiesTtl)));
        
        cacheConfigurations.put("translations", defaultConfig
            .entryTtl(Duration.ofSeconds(translationTtl)));
        
        cacheConfigurations.put("ai-responses", defaultConfig
            .entryTtl(Duration.ofSeconds(summaryTtl)));
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
}
