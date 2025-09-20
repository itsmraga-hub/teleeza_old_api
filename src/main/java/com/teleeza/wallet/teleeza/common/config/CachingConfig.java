package com.teleeza.wallet.teleeza.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("subscription"),
                new ConcurrentMapCache("recent_transactions"),
                new ConcurrentMapCache("referrals"),
                new ConcurrentMapCache("user_details"),
                new ConcurrentMapCache("expenses"),
                new ConcurrentMapCache("income")
        ));
        return cacheManager;
    }
}
