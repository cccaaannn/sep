package com.kurtcan.sepproductservice.shared.cache;

import com.kurtcan.sepproductservice.shared.constant.ProfileName;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!" + ProfileName.TEST)
@ConditionalOnProperty(prefix = "redis", name = "enabled", havingValue = "false", matchIfMissing = true)
public class DefaultMapCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CacheName.PRODUCTS);
    }

}
