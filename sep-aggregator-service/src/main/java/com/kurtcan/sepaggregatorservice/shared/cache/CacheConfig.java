package com.kurtcan.sepaggregatorservice.shared.cache;

import com.kurtcan.sepaggregatorservice.shared.constant.ProfileName;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@EnableCaching
@Configuration
@Profile("!" + ProfileName.TEST)
public class CacheConfig {
}
