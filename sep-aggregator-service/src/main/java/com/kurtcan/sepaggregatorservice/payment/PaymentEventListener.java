package com.kurtcan.sepaggregatorservice.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kurtcan.sepaggregatorservice.payment.event.PaymentCreated;
import com.kurtcan.sepaggregatorservice.shared.cache.CacheName;
import com.kurtcan.sepaggregatorservice.shared.cache.CacheUtils;
import com.kurtcan.sepaggregatorservice.shared.constant.ProfileName;
import com.kurtcan.sepaggregatorservice.shared.event.DataEvent;
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
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!" + ProfileName.TEST)
public class PaymentEventListener {

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
    @KafkaListener(topics = PaymentEventTopic.PAYMENT_CREATED)
    public void created(String message) {
        log.info("Received event {}:{}", PaymentEventTopic.PAYMENT_CREATED, message);

        Optional<DataEvent<PaymentCreated>> event = deserializeEvent(message);
        if (event.isEmpty()) return;

        UUID userId = event.get().getData().userId();

        log.info("Invalidating cache {} and {} for user {}", CacheName.PAYMENTS, CacheName.PRODUCTS, userId);
        cacheUtils.evictAllWithPrefix(CacheName.PAYMENTS, userId.toString());
        cacheUtils.evictAllWithPrefix(CacheName.PRODUCTS, userId.toString());
    }

    private Optional<DataEvent<PaymentCreated>> deserializeEvent(String message) {
        try {
            var paymentCreatedEVentType = new TypeReference<DataEvent<PaymentCreated>>() {
            };
            return Optional.of(objectMapper.readValue(message, paymentCreatedEVentType));
        } catch (JsonProcessingException e) {
            log.error("Error while deserializing message: {}", e.getMessage());
            return Optional.empty();
        }
    }

}
