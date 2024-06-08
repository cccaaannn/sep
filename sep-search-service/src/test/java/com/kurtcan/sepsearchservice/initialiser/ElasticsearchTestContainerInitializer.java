package com.kurtcan.sepsearchservice.initialiser;

import org.junit.ClassRule;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class ElasticsearchTestContainerInitializer {

    private static final String ELASTICSEARCH_DOCKER_VERSION = "elasticsearch:8.13.0";

    @ClassRule
    public static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer(ELASTICSEARCH_DOCKER_VERSION)
            .withEnv("discovery.type", "single-node")
            .withEnv("cluster.name", "elasticsearch")
            .withEnv("xpack.security.enabled", Boolean.FALSE.toString());

    static {
        elasticsearchContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("elasticsearch.host", elasticsearchContainer::getHost);
        registry.add("elasticsearch.port", elasticsearchContainer::getFirstMappedPort);
    }

}
