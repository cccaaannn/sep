package com.kurtcan.sepproductservice.shared.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "security-global")
public class SecurityGlobalProperties {
    private List<String> whiteListPaths;
}
