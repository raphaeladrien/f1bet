package com.sporty.f1bet.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sporty.f1bet.dto.SessionResponse;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaffeineConfig {
    @Bean
    public Cache<String, SessionResponse> eventCache() {
        return Caffeine.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).build();
    }
}
