package com.kurtcan.sepaggregatorservice.shared.cache.hazelcast;

import com.hazelcast.config.*;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.eureka.one.EurekaOneDiscoveryStrategyFactory;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import com.kurtcan.sepaggregatorservice.shared.constant.ProfileName;
import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.text.MessageFormat;

@Configuration
@RequiredArgsConstructor
@Profile("!" + ProfileName.TEST)
@ConditionalOnProperty(prefix = "hazelcast", name = "enabled", havingValue = "true", matchIfMissing = true)
public class HazelcastCacheConfig {

    private final HazelcastProperties hazelcastProperties;
    private final CustomHazelcastSerializer customHazelcastSerializer;

    @Bean
    public Config getHazelcastConfig(EurekaClient eurekaClient) {

        EurekaOneDiscoveryStrategyFactory.setEurekaClient(eurekaClient);

        // Unique instance name for each instance
        String instanceName = MessageFormat.format("{0}{1}", HazelcastConstants.HAZELCAST_INSTANCE_NAME_PREFIX, System.currentTimeMillis());

        Config config = new Config();
        config.setInstanceName(instanceName);

        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);

        config.getNetworkConfig().getJoin().getEurekaConfig()
                .setEnabled(true)
                .setProperty("self-registration", "true")
                .setProperty("namespace", HazelcastConstants.HAZELCAST_EUREKA_CACHE_NAMESPACE)
                .setProperty("use-metadata-for-host-and-port", "true"); // Use Eureka metadata for host and port

        MapConfig mapConfig = new MapConfig()
                .setName(HazelcastConstants.HAZELCAST_MAP_NAME)
                .setTimeToLiveSeconds(hazelcastProperties.getCache().getCacheTtlSeconds())
                .setBackupCount(hazelcastProperties.getCache().getBackupCount())
                .setEvictionConfig(
                        new EvictionConfig()
                                .setEvictionPolicy(EvictionPolicy.LRU)
                                .setMaxSizePolicy(MaxSizePolicy.FREE_HEAP_SIZE)
                                .setSize(hazelcastProperties.getCache().getMaxSize())
                );
        config.addMapConfig(mapConfig);

        // Register custom serializer, to fix generic type issue
        SerializationConfig serializationConfig = config.getSerializationConfig();
        SerializerConfig serializerConfig = new SerializerConfig()
                .setTypeClass(Object.class)
                .setImplementation(customHazelcastSerializer);
        serializationConfig.addSerializerConfig(serializerConfig);

        return config;
    }

    @Bean
    public HazelcastCacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }

}