package com.kurtcan.seppaymentservice.shared.circuitbreaker;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CircuitBreakerConfig {

    @Bean(CircuitBreakerName.PRODUCT)
    public CircuitBreaker productServiceCircuitBreaker(Resilience4JCircuitBreakerFactory circuitBreakerFactory) {
        return circuitBreakerFactory.create(CircuitBreakerName.PRODUCT);
    }

    @Bean(CircuitBreakerName.TOKEN)
    public CircuitBreaker tokenClientCircuitBreaker(Resilience4JCircuitBreakerFactory circuitBreakerFactory) {
        return circuitBreakerFactory.create(CircuitBreakerName.TOKEN);
    }
}
