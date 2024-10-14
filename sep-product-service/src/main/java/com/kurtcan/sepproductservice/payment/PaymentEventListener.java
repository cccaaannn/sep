package com.kurtcan.sepproductservice.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kurtcan.sepproductservice.product.Product;
import com.kurtcan.sepproductservice.product.ProductRepository;
import com.kurtcan.sepproductservice.shared.circuitbreaker.CircuitBreakerName;
import com.kurtcan.sepproductservice.shared.constant.ProfileName;
import com.kurtcan.sepproductservice.shared.event.SimpleEvent;
import com.kurtcan.sepproductservice.shared.jwt.TokenClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy;
import org.springframework.kafka.support.converter.ConversionException;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!" + ProfileName.TEST)
public class PaymentEventListener {

    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;
    private final PaymentServiceClient paymentServiceClient;
    @Qualifier(CircuitBreakerName.PAYMENT)
    private final CircuitBreaker paymentServiceCircuitBreaker;
    private final TokenClient tokenClient;

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
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

        Optional<SimpleEvent> event = deserializeEvent(message);
        if (event.isEmpty()) return;

        Optional<TokenClient.TokenResponse> tokenOptional = tokenClient.getTokenWithCircuitBreaker();
        if (tokenOptional.isEmpty()) {
            log.error("Token not found");
            return;
        }
        log.info("Acquired access token: {}", tokenOptional.get().accessToken());

        Optional<Payment> paymentOptional = paymentServiceCircuitBreaker.run(
                () -> paymentServiceClient.getPaymentById(
                        event.get().getId(),
                        MessageFormat.format("Bearer {0}", tokenOptional.get().accessToken())
                ),
                throwable -> {
                    log.error("Error while fetching payment from service: {}", throwable.getMessage());
                    return Optional.empty();
                });

        if (paymentOptional.isEmpty()) {
            log.error("Payment not found with id: {}", event.get().getId());
            return;
        }

        Payment payment = paymentOptional.get();

        Optional<Product> productOptional = productRepository.findById(payment.getProductId());

        if (productOptional.isEmpty()) {
            log.error("Product not found with id: {}", payment.getProductId());
            return;
        }

        Product product = productOptional.get();
        if (payment.getAmount() > product.getStockAmount()) {
            log.error("Not enough stock for payment: {}", payment);
            return;
        }

        product.setStockAmount(product.getStockAmount() - payment.getAmount());

        productRepository.save(productOptional.get());
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
