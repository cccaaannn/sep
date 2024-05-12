package com.kurtcan.seppaymentservice.shared.log;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "logstash")
public class LogstashProperties {
    private String host;
    private int port;
}
