package com.kurtcan.sepsearchservice.shared.event;

public interface JsonEventPublisher {
    void publish(String topic, SimpleEvent event);
    void publish(String topic, Object event);
}
