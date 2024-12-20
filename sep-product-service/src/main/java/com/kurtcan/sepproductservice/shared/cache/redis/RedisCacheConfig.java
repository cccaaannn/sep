package com.kurtcan.sepproductservice.shared.cache.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kurtcan.sepproductservice.shared.constant.ProfileName;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@Profile("!" + ProfileName.TEST)
@ConditionalOnProperty(prefix = "redis", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedisCacheConfig {

    @Qualifier("redisObjectMapper")
    private final ObjectMapper redisObjectMapper;
    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(redisProperties.getDefaultCacheTtlMinutes()))
                .disableCachingNullValues()
                .prefixCacheNameWith(redisProperties.getCacheNamePrefix())
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper)));
    }

}
