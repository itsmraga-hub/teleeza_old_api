package com.teleeza.wallet.teleeza.rateconfig;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RateLimitConfig {
    private final Map<String, RateLimiter> clientRateLimiters = new HashMap<>();

    @Bean
    public Map<String, RateLimiter> clientRateLimiters() {
        return clientRateLimiters;
    }

    public static RateLimiter createRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .limitForPeriod(3)
                .timeoutDuration(Duration.ofMillis(100))
                .build();
        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        return registry.rateLimiter("myRateLimiter");
    }

    public RateLimiter getOrCreateRateLimiter(String clientId) {
        return clientRateLimiters.computeIfAbsent(clientId, id -> {
            RateLimiterConfig config = RateLimiterConfig.custom()
                    .limitRefreshPeriod(Duration.ofMinutes(1))
                    .limitForPeriod(10)
                    .timeoutDuration(Duration.ofMillis(100))
                    .build();
            return RateLimiterRegistry.of(config).rateLimiter(id);
        });
    }
}
