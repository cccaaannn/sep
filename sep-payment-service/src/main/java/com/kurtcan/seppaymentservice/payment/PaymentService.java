package com.kurtcan.seppaymentservice.payment;

import com.kurtcan.seppaymentservice.payment.request.PaymentCreate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PaymentService {
    Flux<Payment> getAllPayments();

    Mono<Payment> getPaymentById(UUID id);

    Mono<Payment> createPayment(PaymentCreate payment);
}