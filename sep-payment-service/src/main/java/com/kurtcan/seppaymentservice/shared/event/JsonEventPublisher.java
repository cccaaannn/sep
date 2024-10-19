package com.kurtcan.seppaymentservice.shared.event;

import reactor.core.publisher.Mono;

public interface JsonEventPublisher {
    void publish(String topic, SimpleEvent event);

    <T> void publish(String topic, DataEvent<T> event);

    void publish(String topic, Object event);

    Mono<Void> publishAsync(String topic, SimpleEvent event);

    <T> Mono<Void> publishAsync(String topic, DataEvent<T> event);

    Mono<Void> publishAsync(String topic, Object event);
}
