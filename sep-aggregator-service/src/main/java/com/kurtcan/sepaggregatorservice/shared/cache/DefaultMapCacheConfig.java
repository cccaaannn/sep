package com.kurtcan.sepaggregatorservice.shared.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "hazelcast", name = "enabled", havingValue = "false", matchIfMissing = true)
public class DefaultMapCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CacheName.PRODUCTS, CacheName.PAYMENTS);
    }

}
