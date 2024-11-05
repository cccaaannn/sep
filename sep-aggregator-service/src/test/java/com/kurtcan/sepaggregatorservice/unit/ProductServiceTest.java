package com.kurtcan.sepaggregatorservice.unit;

import com.kurtcan.sepaggregatorservice.payment.Payment;
import com.kurtcan.sepaggregatorservice.payment.PaymentService;
import com.kurtcan.sepaggregatorservice.prodcut.*;
import com.kurtcan.sepaggregatorservice.shared.jwt.TokenClient;
import com.kurtcan.sepaggregatorservice.shared.pagaination.PageImpl;
import com.kurtcan.sepaggregatorservice.shared.pagaination.PageMapper;
import com.kurtcan.sepaggregatorservice.shared.security.CurrentUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private final PageMapper pageMapper;
    private final TokenClient tokenClient;
    private final CurrentUserService currentUserService;
    private final CircuitBreaker productServiceCircuitBreaker;
    private final PaymentService paymentService;

    private final ProductService productService;

    public ProductServiceTest() {
        pageMapper = mock(PageMapper.class);
        tokenClient = mock(TokenClient.class);
        currentUserService = mock(CurrentUserService.class);
        productServiceCircuitBreaker = mock(CircuitBreaker.class);
        ProductServiceClient productServiceClient = mock(ProductServiceClient.class);
        paymentService = mock(PaymentService.class);

        productService = ProductServiceImpl.builder()
                .pageMapper(pageMapper)
                .tokenClient(tokenClient)
                .currentUserService(currentUserService)
                .productServiceCircuitBreaker(productServiceCircuitBreaker)
                .productServiceClient(productServiceClient)
                .paymentService(paymentService)
                .build();
    }

    @Test
    public void testGetProductWithPayments() {
        // Given

        // Get current suer
        var userId = UUID.randomUUID();
        var currentUser = CurrentUserService.CurrentUser.builder().id(userId).build();
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));

        // Get token
        var tokenResponse = new TokenClient.TokenResponse("token", "token", 1000);
        when(tokenClient.getTokenWithCircuitBreaker()).thenReturn(Optional.of(tokenResponse));

        // Get products
        var productId = UUID.randomUUID();
        var product = Product.builder().id(productId).build();
        var products = List.of(product);
        var productPage = PageImpl.<Product>builder().content(products).build();
        when(productServiceCircuitBreaker.run(any(), any())).thenReturn(Optional.of(productPage));

        // Get payments
        var paymentId = UUID.randomUUID();
        var payment = Payment.builder().id(paymentId).userId(userId).amount(100).build();
        var payments = List.of(payment);
        when(paymentService.getUserPayments(userId)).thenReturn(payments);

        // Map products to ProductWithPayments
        var productWithPayments = ProductWithPayments.builder().id(productId).payments(payments).build();
        var productsWithPayments = List.of(productWithPayments);
        var productsWithPaymentsPage = PageImpl.<ProductWithPayments>builder().content(productsWithPayments).build();
        when(pageMapper.mapPaginatedList(productPage, ProductWithPayments.class)).thenReturn(productsWithPaymentsPage);

        // When
        PageImpl<ProductWithPayments> productsResult = productService.getProductWithPayments("search", 1, 10, "sort", "order");

        // Then
        verify(currentUserService, times(1)).getCurrentUser();
        verify(tokenClient, times(1)).getTokenWithCircuitBreaker();
        verify(productServiceCircuitBreaker, times(1)).run(any(), any());
        verify(paymentService, times(1)).getUserPayments(userId);
        verify(pageMapper, times(1)).mapPaginatedList(any(), any());
        Assertions.assertEquals(productsWithPaymentsPage, productsResult);
    }

    @Test
    public void testGetProductWithPaymentsNoProducts() {
        // Given

        // Get current suer
        var userId = UUID.randomUUID();
        var currentUser = CurrentUserService.CurrentUser.builder().id(userId).build();
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));

        // Get token
        var tokenResponse = new TokenClient.TokenResponse("token", "token", 1000);
        when(tokenClient.getTokenWithCircuitBreaker()).thenReturn(Optional.of(tokenResponse));

        // Get products
        when(productServiceCircuitBreaker.run(any(), any())).thenReturn(Optional.empty());

        // Get payments
        var paymentId = UUID.randomUUID();
        var payment = Payment.builder().id(paymentId).userId(userId).amount(100).build();
        var payments = List.of(payment);
        when(paymentService.getUserPayments(userId)).thenReturn(payments);

        // When
        PageImpl<ProductWithPayments> productsResult = productService.getProductWithPayments("search", 1, 10, "sort", "order");

        // Then
        verify(currentUserService, times(1)).getCurrentUser();
        verify(tokenClient, times(1)).getTokenWithCircuitBreaker();
        verify(productServiceCircuitBreaker, times(1)).run(any(), any());
        verify(paymentService, times(1)).getUserPayments(userId);
        Assertions.assertEquals(PageImpl.empty(), productsResult);
    }
}
