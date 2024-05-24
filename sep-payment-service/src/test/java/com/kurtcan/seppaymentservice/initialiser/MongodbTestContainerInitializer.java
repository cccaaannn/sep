package com.kurtcan.seppaymentservice.initialiser;

import org.junit.ClassRule;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

public class MongodbTestContainerInitializer {

    private static final String MONGODB_DOCKER_VERSION = "mongo:7.0.8";

    @ClassRule
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer(MONGODB_DOCKER_VERSION)
            .withEnv("MONGO_INITDB_DATABASE", "sep-payment-service")
            .withEnv("MONGO_INIT_ROOT_USERNAME", "admin")
            .withEnv("MONGO_INIT_ROOT_PASSWORD", "admin");

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
}
