package com.kurtcan.sepproductservice.shared.event;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    @NestedConfigurationProperty
    private List<String> bootstrapServers;

    @NestedConfigurationProperty
    private Consumer consumer;

    @Data
    public static class Consumer {
        private String groupId;
    }
}
