package com.kurtcan.sepaggregatorservice.unit;

import com.kurtcan.sepaggregatorservice.payment.Payment;
import com.kurtcan.sepaggregatorservice.payment.PaymentService;
import com.kurtcan.sepaggregatorservice.payment.PaymentServiceClient;
import com.kurtcan.sepaggregatorservice.payment.PaymentServiceImpl;
import com.kurtcan.sepaggregatorservice.shared.jwt.TokenClient;
import com.kurtcan.sepaggregatorservice.shared.security.CurrentUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    private final TokenClient tokenClient;
    private final CurrentUserService currentUserService;
    private final CircuitBreaker paymentServiceCircuitBreaker;

    private final PaymentService paymentService;

    public PaymentServiceTest() {
        tokenClient = mock(TokenClient.class);
        currentUserService = mock(CurrentUserService.class);
        paymentServiceCircuitBreaker = mock(CircuitBreaker.class);
        PaymentServiceClient paymentServiceClient = mock(PaymentServiceClient.class);

        paymentService = PaymentServiceImpl.builder()
                .tokenClient(tokenClient)
                .currentUserService(currentUserService)
                .paymentServiceCircuitBreaker(paymentServiceCircuitBreaker)
                .paymentServiceClient(paymentServiceClient)
                .build();
    }

    @Test
    public void testGetUserPayments() {
        // Given

        // Get current suer
        var userId = UUID.randomUUID();
        var currentUser = CurrentUserService.CurrentUser.builder().id(userId).build();
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));

        // Get token
        var tokenResponse = new TokenClient.TokenResponse("token", "token", 1000);
        when(tokenClient.getTokenWithCircuitBreaker()).thenReturn(Optional.of(tokenResponse));

        var paymentId = UUID.randomUUID();
        var payment = Payment.builder().id(paymentId).userId(userId).amount(100).build();
        var payments = List.of(payment);
        when(paymentServiceCircuitBreaker.run(any(), any())).thenReturn(Optional.of(payments));

        // When
        var userPayments = paymentService.getUserPayments();

        // Then
        verify(currentUserService, times(1)).getCurrentUser();
        verify(tokenClient, times(1)).getTokenWithCircuitBreaker();
        verify(paymentServiceCircuitBreaker, times(1)).run(any(), any());
        Assertions.assertEquals(payments, userPayments);
    }

    @Test
    public void testGetUserPaymentsByUserId() {
        // Given
        var userId = UUID.randomUUID();

        // Get token
        var tokenResponse = new TokenClient.TokenResponse("token", "token", 1000);
        when(tokenClient.getTokenWithCircuitBreaker()).thenReturn(Optional.of(tokenResponse));

        var paymentId = UUID.randomUUID();
        var payment = Payment.builder().id(paymentId).userId(userId).amount(100).build();
        var payments = List.of(payment);
        when(paymentServiceCircuitBreaker.run(any(), any())).thenReturn(Optional.of(payments));

        // When
        var userPayments = paymentService.getUserPayments(userId);

        // Then
        verify(tokenClient, times(1)).getTokenWithCircuitBreaker();
        verify(paymentServiceCircuitBreaker, times(1)).run(any(), any());
        Assertions.assertEquals(payments, userPayments);
    }

}
