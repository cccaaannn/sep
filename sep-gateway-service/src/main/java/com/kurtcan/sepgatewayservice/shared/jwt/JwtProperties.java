package com.kurtcan.sepgatewayservice.shared.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String publicKeyBase64;
    private int allowedClockSkewInSeconds;
    private String issuer;
}
