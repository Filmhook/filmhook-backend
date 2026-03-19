package com.annular.filmhook.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)   // cache lifetime
                .maximumSize(5000)                        // max cached items
                .weakKeys()
                .recordStats();
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);

        // Register cache names if needed
        cacheManager.setCacheNames(
                java.util.List.of(
                        "propertyCache",      // For Shooting Location Property
                        "userCache",          // Example: user profile
                        "categoryCache",
                        "securityQuestionsCache"
                )
        );

        return cacheManager;
    }
}
