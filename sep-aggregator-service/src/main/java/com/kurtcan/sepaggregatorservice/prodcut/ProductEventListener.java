package com.kurtcan.sepaggregatorservice.prodcut;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kurtcan.sepaggregatorservice.shared.cache.CacheName;
import com.kurtcan.sepaggregatorservice.shared.cache.CacheUtils;
import com.kurtcan.sepaggregatorservice.shared.constant.ProfileName;
import com.kurtcan.sepaggregatorservice.shared.event.SimpleEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy;
import org.springframework.kafka.support.converter.ConversionException;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!" + ProfileName.TEST)
public class ProductEventListener {

    private final ObjectMapper objectMapper;
    private final CacheUtils cacheUtils;

    @Transactional
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
    @KafkaListener(topics = {ProductEventTopic.PRODUCT_CREATED, ProductEventTopic.PRODUCT_UPDATED, ProductEventTopic.PRODUCT_DELETED})
    public void created(String message) {
        log.info("Received event {}", message);

        Optional<SimpleEvent> event = deserializeEvent(message);
        if (event.isEmpty()) return;

        log.info("Invalidating cache: {}", CacheName.PRODUCTS);
        cacheUtils.evictAll(CacheName.PRODUCTS);
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
