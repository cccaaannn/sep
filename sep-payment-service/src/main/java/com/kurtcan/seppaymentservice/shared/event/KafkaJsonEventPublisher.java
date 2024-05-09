package com.kurtcan.seppaymentservice.shared.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaJsonEventPublisher implements JsonEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(String topic, SimpleEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, eventJson);
        } catch (JsonProcessingException e) {
            log.error("Error while publishing event: {}", e.getMessage());
        }
    }

    @Override
    public void publish(String topic, Object event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, eventJson);
        } catch (JsonProcessingException e) {
            log.error("Error while publishing event: {}", e.getMessage());
        }
    }

    @Override
    public Mono<Void> publishAsync(String topic, SimpleEvent event) {
        return Mono.fromRunnable(() -> publish(topic, event));
    }

    @Override
    public Mono<Void> publishAsync(String topic, Object event) {
        return Mono.fromRunnable(() -> publish(topic, event));
    }
}
