package com.kurtcan.seppaymentservice.unit.payment;

import com.kurtcan.seppaymentservice.payment.*;
import com.kurtcan.seppaymentservice.payment.request.PaymentCreate;
import com.kurtcan.seppaymentservice.product.Product;
import com.kurtcan.seppaymentservice.product.ProductRepository;
import com.kurtcan.seppaymentservice.shared.mapper.ModelMapperConfig;
import com.kurtcan.seppaymentservice.shared.event.JsonEventPublisher;
import com.kurtcan.seppaymentservice.shared.event.SimpleEvent;
import com.kurtcan.seppaymentservice.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    private final ArgumentCaptor<Product> productCaptor;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final JsonEventPublisher eventPublisher;

    public PaymentServiceTest() {
        paymentRepository = mock(PaymentRepository.class);
        productRepository = mock(ProductRepository.class);
        eventPublisher = mock(JsonEventPublisher.class);

        productCaptor = ArgumentCaptor.forClass(Product.class);

        ModelMapperConfig modelMapperConfig = new ModelMapperConfig();

        paymentService = PaymentServiceImpl.builder().
                paymentRepository(paymentRepository).
                productRepository(productRepository).
                eventPublisher(eventPublisher).
                mapper(modelMapperConfig.getModelMapper()).
                build();
    }

    @Test
    public void testCreatePayment() {
        // Given
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int amount = 10;
        BigDecimal price = BigDecimal.valueOf(100);
        int stockAmount = 100;

        Product product = Product.builder()
                .id(productId)
                .stockAmount(stockAmount)
                .build();

        Payment payment = Payment.builder()
                .id(id)
                .userId(userId)
                .productId(productId)
                .amount(amount)
                .price(price)
                .build();

        PaymentCreate paymentCreate = PaymentCreate.builder()
                .userId(userId)
                .productId(productId)
                .amount(amount)
                .build();

        // When
        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));
        when(paymentRepository.save(any(Payment.class))).thenReturn(Mono.just(payment));
        when(eventPublisher.publishAsync(PaymentEventTopic.CREATED, SimpleEvent.fromEntity(payment))).thenReturn(Mono.empty());

        // Then
        StepVerifier.create(paymentService.createPayment(paymentCreate))
                .assertNext(savedPayment -> {
                    Assertions.assertEquals(id, savedPayment.getId());
                    Assertions.assertEquals(userId, savedPayment.getUserId());
                    Assertions.assertEquals(productId, savedPayment.getProductId());
                    Assertions.assertEquals(amount, savedPayment.getAmount());
                    Assertions.assertEquals(price, savedPayment.getPrice());
                })
                .verifyComplete();

        // Verify
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productRepository, times(1)).save(productCaptor.capture());
        verify(eventPublisher, times(1)).publishAsync(PaymentEventTopic.CREATED, SimpleEvent.fromEntity(payment));
        Assertions.assertEquals(stockAmount - amount, productCaptor.getValue().getStockAmount());
    }

    @Test
    public void testCreatePaymentProductNotFound() {
        // Given
        UUID productId = UUID.randomUUID();
        PaymentCreate paymentCreate = PaymentCreate.builder()
                .productId(productId)
                .build();

        // When
        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        // Then
        StepVerifier.create(paymentService.createPayment(paymentCreate))
                .expectError(ResourceNotFoundException.class)
                .verify();

        // Verify
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    public void testCreatePaymentNotEnoughStock() {
        // Given
        UUID productId = UUID.randomUUID();
        int stockAmount = 10;
        int amount = 100;

        Product product = Product.builder()
                .id(productId)
                .stockAmount(stockAmount)
                .build();

        PaymentCreate paymentCreate = PaymentCreate.builder()
                .productId(productId)
                .amount(amount)
                .build();

        // When
        when(productRepository.findById(productId)).thenReturn(Mono.just(product));

        // Then
        StepVerifier.create(paymentService.createPayment(paymentCreate))
                .expectError(IllegalArgumentException.class)
                .verify();

        // Verify
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    public void testGetAllPayments() {
        // Given
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int amount = 10;
        BigDecimal price = BigDecimal.valueOf(100);

        Payment payment = Payment.builder()
                .id(id)
                .userId(userId)
                .productId(productId)
                .amount(amount)
                .price(price)
                .build();

        List<Payment> paymentList = List.of(payment);

        // When
        when(paymentRepository.findAll()).thenReturn(Flux.fromIterable(paymentList));

        // Then
        StepVerifier.create(paymentService.getAllPayments())
                .assertNext(savedPayment -> {
                    Assertions.assertEquals(id, savedPayment.getId());
                    Assertions.assertEquals(userId, savedPayment.getUserId());
                    Assertions.assertEquals(productId, savedPayment.getProductId());
                    Assertions.assertEquals(amount, savedPayment.getAmount());
                    Assertions.assertEquals(price, savedPayment.getPrice());
                })
                .verifyComplete();

        // Verify
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    public void testGetPaymentById() {
        // Given
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int amount = 10;
        BigDecimal price = BigDecimal.valueOf(100);

        Payment payment = Payment.builder()
                .id(id)
                .userId(userId)
                .productId(productId)
                .amount(amount)
                .price(price)
                .build();

        // When
        when(paymentRepository.findById(id)).thenReturn(Mono.just(payment));

        // Then
        StepVerifier.create(paymentService.getPaymentById(id))
                .assertNext(savedPayment -> {
                    Assertions.assertEquals(id, savedPayment.getId());
                    Assertions.assertEquals(userId, savedPayment.getUserId());
                    Assertions.assertEquals(productId, savedPayment.getProductId());
                    Assertions.assertEquals(amount, savedPayment.getAmount());
                    Assertions.assertEquals(price, savedPayment.getPrice());
                })
                .verifyComplete();

        // Verify
        verify(paymentRepository, times(1)).findById(id);
    }

    @Test
    public void testGetPaymentsByUserId() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int amount = 10;
        BigDecimal price = BigDecimal.valueOf(100);

        Payment payment = Payment.builder()
                .id(id)
                .userId(userId)
                .productId(productId)
                .amount(amount)
                .price(price)
                .build();

        List<Payment> paymentList = List.of(payment);

        // When
        when(paymentRepository.findByUserId(userId)).thenReturn(Flux.fromIterable(paymentList));

        // Then
        StepVerifier.create(paymentService.getByUserId(userId))
                .assertNext(savedPayment -> {
                    Assertions.assertEquals(id, savedPayment.getId());
                    Assertions.assertEquals(userId, savedPayment.getUserId());
                    Assertions.assertEquals(productId, savedPayment.getProductId());
                    Assertions.assertEquals(amount, savedPayment.getAmount());
                    Assertions.assertEquals(price, savedPayment.getPrice());
                })
                .verifyComplete();

        // Verify
        verify(paymentRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void testGetPaymentByIdNotFound() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        when(paymentRepository.findById(id)).thenReturn(Mono.empty());

        // Then
        StepVerifier.create(paymentService.getPaymentById(id))
                .expectError(ResourceNotFoundException.class)
                .verify();

        // Verify
        verify(paymentRepository, times(1)).findById(id);
    }

}
