package com.kurtcan.sepsearchservice.shared.elasticsearch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchProperties {
    private String host;
    private int port;
}
