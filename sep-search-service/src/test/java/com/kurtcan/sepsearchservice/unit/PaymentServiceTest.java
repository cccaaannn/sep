package com.kurtcan.sepsearchservice.unit;

import com.kurtcan.sepsearchservice.payment.Payment;
import com.kurtcan.sepsearchservice.payment.PaymentElasticIndex;
import com.kurtcan.sepsearchservice.payment.PaymentService;
import com.kurtcan.sepsearchservice.payment.PaymentServiceImpl;
import com.kurtcan.sepsearchservice.shared.elasticsearch.SimpleElasticsearchClient;
import com.kurtcan.sepsearchservice.shared.elasticsearch.SimpleElasticsearchClientImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    private final PaymentService paymentService;

    private final SimpleElasticsearchClient elasticsearchClient;

    public PaymentServiceTest() {
        elasticsearchClient = mock(SimpleElasticsearchClientImpl.class);
        this.paymentService = new PaymentServiceImpl(elasticsearchClient);
    }

    @Test
    public void testSearchMatchPayment() {
        // Given
        List<Payment> payments = List.of(
                new Payment(UUID.randomUUID(), UUID.randomUUID(), 10, BigDecimal.ONE),
                new Payment(UUID.randomUUID(), UUID.randomUUID(), 10, BigDecimal.ONE)
        );

        int amount = 10;
        String amountStr = "10";

        // When
        when(elasticsearchClient.match(PaymentElasticIndex.NAME, "amount", amountStr, Payment.class)).thenReturn(Optional.of(payments));

        // Then
        List<Payment> searchedProducts = paymentService.match(amount);

        // Verify
        verify(elasticsearchClient, times(1)).match(PaymentElasticIndex.NAME, "amount", amountStr, Payment.class);
        Assertions.assertEquals(2, searchedProducts.size());
        Assertions.assertEquals(amount, searchedProducts.getFirst().getAmount());
    }

    @Test
    public void testSearchMatchPaymentEmpty() {
        // Given
        int amount = 10;
        String amountStr = "10";

        // When
        when(elasticsearchClient.match(PaymentElasticIndex.NAME, "amount", amountStr, Payment.class)).thenReturn(Optional.empty());

        // Then
        List<Payment> searchedProducts = paymentService.match(amount);

        // Verify
        verify(elasticsearchClient, times(1)).match(PaymentElasticIndex.NAME, "amount", amountStr, Payment.class);
        Assertions.assertTrue(searchedProducts.isEmpty());
    }

}
