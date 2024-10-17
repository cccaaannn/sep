package com.kurtcan.sepproductservice.shared.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {
    private boolean enabled;
    private String host;
    private int port;
    private String cacheNamePrefix;
    private int defaultCacheTtlMinutes;
}
