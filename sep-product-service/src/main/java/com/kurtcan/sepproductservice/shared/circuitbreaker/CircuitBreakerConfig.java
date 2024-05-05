package com.kurtcan.sepproductservice.shared.circuitbreaker;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CircuitBreakerConfig {

    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;

    @Bean(CircuitBreakerName.PAYMENT)
    public CircuitBreaker paynemtServiceCircuitBreaker() {
        return circuitBreakerFactory.create(CircuitBreakerName.PAYMENT);
    }

}
