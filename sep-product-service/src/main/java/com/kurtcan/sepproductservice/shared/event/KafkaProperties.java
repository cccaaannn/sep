package com.kurtcan.sepproductservice.shared.event;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "kafka.bootstrap-server")
public class KafkaProperties {
    private String url;
    private int port;
    private String consumerGroupId;
}
