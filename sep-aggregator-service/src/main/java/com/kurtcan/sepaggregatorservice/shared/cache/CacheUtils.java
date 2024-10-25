package com.kurtcan.sepaggregatorservice.shared.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheUtils {

    private final CacheManager cacheManager;

    public void evictAll(String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (Objects.isNull(cache)) {
            log.info("Cache {} not found", cacheName);
            return;
        }

        cache.clear();
    }

    public void evictAllWithPrefix(String cacheName, String cacheKeyPrefix) {
        var cache = cacheManager.getCache(cacheName);
        if (Objects.isNull(cache)) {
            log.info("Cache {} not found", cacheName);
            return;
        }

        var cacheNative = cache.getNativeCache();
        if (cacheNative instanceof java.util.concurrent.ConcurrentMap<?, ?> nativeCache) {
            nativeCache.keySet().stream()
                    .filter(key -> key.toString().startsWith(cacheKeyPrefix))
                    .forEach(cache::evict);
        } else {
            log.error("Cache implementation does not support direct access to keys");
        }
    }
}
