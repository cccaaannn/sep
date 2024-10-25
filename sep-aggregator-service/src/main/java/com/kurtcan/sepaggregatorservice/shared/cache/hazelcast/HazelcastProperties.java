package com.kurtcan.sepaggregatorservice.shared.cache.hazelcast;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "hazelcast")
public class HazelcastProperties {
    private boolean enabled;

    @NestedConfigurationProperty
    private CacheProperties cache;

    @Data
    @ConfigurationProperties(prefix = "hazelcast.cache")
    public static class CacheProperties {
        private int cacheTtlSeconds;
        private int backupCount;
        private int maxSize;
    }
}
