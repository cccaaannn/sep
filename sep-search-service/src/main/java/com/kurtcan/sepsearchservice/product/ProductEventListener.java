package com.kurtcan.sepsearchservice.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kurtcan.sepsearchservice.shared.circuitbreaker.CircuitBreakerName;
import com.kurtcan.sepsearchservice.shared.constant.ProfileName;
import com.kurtcan.sepsearchservice.shared.elasticsearch.SimpleElasticsearchClient;
import com.kurtcan.sepsearchservice.shared.event.SimpleEvent;
import com.kurtcan.sepsearchservice.shared.jwt.TokenClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy;
import org.springframework.kafka.support.converter.ConversionException;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!" + ProfileName.TEST)
public class ProductEventListener {

    private final ObjectMapper objectMapper;
    private final ProductServiceClient productServiceClient;
    @Qualifier(CircuitBreakerName.PRODUCT)
    private final CircuitBreaker productServiceCircuitBreaker;
    private final SimpleElasticsearchClient elasticsearchClient;
    private final TokenClient tokenClient;

    @RetryableTopic(
            kafkaTemplate = "kafkaTemplate",
            sameIntervalTopicReuseStrategy = SameIntervalTopicReuseStrategy.SINGLE_TOPIC,
            retryTopicSuffix = "-retry",
            dltTopicSuffix = "-dlt",
            attempts = "3",
            backoff = @Backoff(
                    delay = 3 * 1000,
                    multiplier = 2,
                    maxDelay = 10 * 60 * 1000
            ),
            exclude = {
                    SerializationException.class,
                    DeserializationException.class,
                    ConversionException.class,
                    NullPointerException.class
            }
    )
    @KafkaListener(topics = ProductEventTopic.PRODUCT_CREATED)
    public void created(String message) {
        log.info("Received event {}:{}", ProductEventTopic.PRODUCT_CREATED, message);

        Optional<SimpleEvent> event = deserializeEvent(message);
        if (event.isEmpty())
            return;

        addOrUpdateProduct(event.get());
    }

    @RetryableTopic(
            kafkaTemplate = "kafkaTemplate",
            sameIntervalTopicReuseStrategy = SameIntervalTopicReuseStrategy.SINGLE_TOPIC,
            retryTopicSuffix = "-retry",
            dltTopicSuffix = "-dlt",
            attempts = "3",
            backoff = @Backoff(
                    delay = 3 * 1000,
                    multiplier = 2,
                    maxDelay = 10 * 60 * 1000
            ),
            exclude = {
                    SerializationException.class,
                    DeserializationException.class,
                    ConversionException.class,
                    NullPointerException.class
            }
    )
    @KafkaListener(topics = ProductEventTopic.PRODUCT_UPDATED)
    public void updated(String message) {
        log.info("Received event {}:{}", ProductEventTopic.PRODUCT_UPDATED, message);

        Optional<SimpleEvent> event = deserializeEvent(message);
        if (event.isEmpty())
            return;

        addOrUpdateProduct(event.get());
    }

    @RetryableTopic(
            kafkaTemplate = "kafkaTemplate",
            sameIntervalTopicReuseStrategy = SameIntervalTopicReuseStrategy.SINGLE_TOPIC,
            retryTopicSuffix = "-retry",
            dltTopicSuffix = "-dlt",
            attempts = "3",
            backoff = @Backoff(
                    delay = 3 * 1000,
                    multiplier = 2,
                    maxDelay = 10 * 60 * 1000
            ),
            exclude = {
                    SerializationException.class,
                    DeserializationException.class,
                    ConversionException.class,
                    NullPointerException.class
            }
    )
    @KafkaListener(topics = ProductEventTopic.PRODUCT_DELETED)
    public void deleted(String message) {
        log.info("Received event {}:{}", ProductEventTopic.PRODUCT_DELETED, message);

        Optional<SimpleEvent> event = deserializeEvent(message);
        if (event.isEmpty())
            return;

        elasticsearchClient.deleteById(ProductElasticIndex.NAME, event.get().getId());
    }

    private void addOrUpdateProduct(SimpleEvent event) {
        Optional<TokenClient.TokenResponse> tokenOptional = tokenClient.getTokenWithCircuitBreaker();
        if (tokenOptional.isEmpty()) {
            log.error("Token not found");
            return;
        }
        log.info("Acquired access token: {}", tokenOptional.get().accessToken());

        Optional<Product> productOptional = productServiceCircuitBreaker.run(
                () -> productServiceClient.getProductById(
                        event.getId(),
                        MessageFormat.format("Bearer {0}", tokenOptional.get().accessToken())),
                throwable -> {
                    log.error("Error while fetching product from service: {}", throwable.getMessage());
                    return Optional.empty();
                });

        if (productOptional.isEmpty()) {
            log.error("Product not found with id: {}", event.getId());
            return;
        }

        Product product = productOptional.get();

        elasticsearchClient.save(ProductElasticIndex.NAME, product);
    }

    private Optional<SimpleEvent> deserializeEvent(String message) {
        try {
            return Optional.of(objectMapper.readValue(message, SimpleEvent.class));
        } catch (JsonProcessingException e) {
            log.error("Error while deserializing message: {}", e.getMessage());
            return Optional.empty();
        }
    }

}
