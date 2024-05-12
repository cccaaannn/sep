package com.kurtcan.seppaymentservice.initialiser;

import org.junit.ClassRule;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

public class MongodbTestContainerInitializer {

    @ClassRule
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.8")
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
