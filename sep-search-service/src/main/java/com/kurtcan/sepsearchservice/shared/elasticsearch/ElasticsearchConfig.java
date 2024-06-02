package com.kurtcan.sepsearchservice.shared.elasticsearch;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfig {

    private final ElasticsearchProperties properties;

    @Bean
    public RestClient client() {
        return RestClient.builder(new HttpHost(properties.getHost(), properties.getPort(), "http")).build();
    }
}