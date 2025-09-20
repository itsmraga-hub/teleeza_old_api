package com.teleeza.wallet.teleeza.common.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableCaching
public class RateLimitingConfig {

    // Define the rate limit: 10 requests per minute
    private static final Bandwidth LIMIT = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));

    // Cache to store buckets for each user (or IP)
    @Bean
    public Map<String, Bucket> bucketCache() {
        return new ConcurrentHashMap<>();
    }

    // Create a bucket for each user (or IP)
    @Bean
    public Bucket createBucket() {
        return Bucket4j.builder()
                .addLimit(LIMIT)
                .build();
    }
}