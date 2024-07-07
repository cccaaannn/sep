package com.kurtcan.seppaymentservice.shared.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "jwt-global")
public class JwtGlobalProperties {
    private String publicKeyBase64;
    private int allowedClockSkewInSeconds;
    private String issuer;
    private String tokenEndpoint;
}
