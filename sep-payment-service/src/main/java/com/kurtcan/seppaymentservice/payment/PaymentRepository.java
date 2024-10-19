package com.kurtcan.seppaymentservice.payment;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface PaymentRepository extends ReactiveMongoRepository<Payment, UUID> {
    Flux<Payment> findByUserId(@Param("userId") UUID userId);
}
