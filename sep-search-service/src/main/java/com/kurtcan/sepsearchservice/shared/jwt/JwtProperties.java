package com.kurtcan.sepsearchservice.shared.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String clientSecret;
    private String clientId;
}
