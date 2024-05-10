package com.kurtcan.seppaymentservice.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kurtcan.seppaymentservice.shared.constant.ProfileName;
import com.kurtcan.seppaymentservice.shared.event.SimpleEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!" + ProfileName.TEST)
public class ProductEventListener {

    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;
    private final ProductServiceClient productServiceClient;
    private final CircuitBreaker productServiceCircuitBreaker;

    @Transactional
    @KafkaListener(topics = ProductEventTopic.PRODUCT_CREATED)
    public void created(String message) {
        log.info("Received event {}:{}", ProductEventTopic.PRODUCT_CREATED, message);

        Optional<SimpleEvent> event = deserializeEvent(message);
        if (event.isEmpty()) return;

        addOrUpdateProduct(event.get());
    }

    @Transactional
    @KafkaListener(topics = ProductEventTopic.PRODUCT_UPDATED)
    public void updated(String message) {
        log.info("Received event {}:{}", ProductEventTopic.PRODUCT_UPDATED, message);

        Optional<SimpleEvent> event = deserializeEvent(message);
        if (event.isEmpty()) return;

        addOrUpdateProduct(event.get());
    }

    @Transactional
    @KafkaListener(topics = ProductEventTopic.PRODUCT_DELETED)
    public void deleted(String message) {
        log.info("Received event {}:{}", ProductEventTopic.PRODUCT_DELETED, message);

        Optional<SimpleEvent> event = deserializeEvent(message);
        if (event.isEmpty()) return;

        productRepository.deleteById(event.get().getId()).subscribe();
    }

    private void addOrUpdateProduct(SimpleEvent event) {
        productServiceCircuitBreaker.run(() -> productServiceClient.getProduct(event.getId()), throwable -> {
                    log.error("Error while fetching product from service: {}", throwable.getMessage());
                    return Mono.<Product>empty();
                })
                .doOnNext(product -> log.debug("Fetched product: {}", product))
                .filter(product -> {
                    if (Objects.isNull(product)) {
                        log.error("Product not found with id: {}", event.getId());
                        return false;
                    }
                    return true;
                })
                .flatMap(product ->
                        productRepository.findById(product.getId())
                                .doOnNext(existingProduct -> log.debug("Found existing product: {}", existingProduct))
                                .defaultIfEmpty(product)
                                .map(existingProduct -> {
                                    mapper.map(product, existingProduct);
                                    return existingProduct;
                                })
                                .flatMap(productRepository::save)
                )
                .doOnNext(savedProduct -> log.debug("Saved product: {}", savedProduct))
                .onErrorResume(throwable -> {
                    log.error("Error while updating product: {}", throwable.getMessage());
                    return Mono.empty();
                })
                .subscribe();
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
