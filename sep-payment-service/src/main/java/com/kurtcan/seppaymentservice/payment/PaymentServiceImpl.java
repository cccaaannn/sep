package com.kurtcan.seppaymentservice.payment;

import com.kurtcan.seppaymentservice.payment.request.PaymentCreate;
import com.kurtcan.seppaymentservice.product.ProductRepository;
import com.kurtcan.seppaymentservice.shared.event.JsonEventPublisher;
import com.kurtcan.seppaymentservice.shared.event.SimpleEvent;
import com.kurtcan.seppaymentservice.shared.exception.ResourceNotFoundException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Builder
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final JsonEventPublisher eventPublisher;
    private final ModelMapper mapper;

    @Override
    public Flux<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Mono<Payment> getPaymentById(UUID id) {
        return paymentRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product not found")));
    }

    @Override
    public Mono<Payment> createPayment(PaymentCreate paymentCreate) {
        return productRepository.findById(paymentCreate.productId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product not found")))
                .flatMap(product -> {
                    if (paymentCreate.amount() > product.getStockAmount()) {
                        return Mono.error(new IllegalArgumentException("Not enough stock"));
                    }
                    Payment payment = mapper.map(paymentCreate, Payment.class);
                    mapper.map(product, payment);
                    payment.setId(UUID.randomUUID());

                    product.setStockAmount(product.getStockAmount() - paymentCreate.amount());

                    return productRepository.save(product)
                            .then(paymentRepository.save(payment))
                            .flatMap(savedPayment -> {
                                eventPublisher.publishAsync(PaymentEventTopic.CREATED, SimpleEvent.fromEntity(savedPayment)).subscribe();
                                return Mono.just(savedPayment);
                            });
                });
    }
}
