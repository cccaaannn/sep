package com.kurtcan.sepsearchservice.integration;

import com.kurtcan.sepsearchservice.initialiser.ElasticsearchTestContainerInitializer;
import com.kurtcan.sepsearchservice.payment.Payment;
import com.kurtcan.sepsearchservice.payment.PaymentElasticIndex;
import com.kurtcan.sepsearchservice.shared.constant.ProfileName;
import com.kurtcan.sepsearchservice.shared.elasticsearch.SimpleElasticsearchClient;
import org.elasticsearch.client.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles(ProfileName.TEST)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentSearchIntegrationTest extends ElasticsearchTestContainerInitializer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SimpleElasticsearchClient elasticsearchClient;

    @AfterEach
    void tearDown() {
        Optional<Response> deleteResult = elasticsearchClient.deleteIndex(PaymentElasticIndex.NAME);
        if (deleteResult.isEmpty() || deleteResult.get().getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed to delete index");
        }
    }

    @Test
    public void testSearchPayment() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int amount = 10;
        BigDecimal price = new BigDecimal("100.00");

        Payment payment = new Payment(userId, productId, amount, price);
        payment.setId(id);
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(Instant.now());

        // Index the payment
        Optional<Response> saveResponse = elasticsearchClient.save(PaymentElasticIndex.NAME, payment);
        Assertions.assertNotNull(saveResponse.get());
        Assertions.assertEquals(201, saveResponse.get().getStatusLine().getStatusCode());

        // Elasticsearch needs some time before querying it back
        Thread.sleep(2000);

        // Then - Verify
        mockMvc.perform(MockMvcRequestBuilders.get("/search/payments/match")
                        .param("amount", String.valueOf(amount))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(payment.getId().toString())))
                .andExpect(jsonPath("$[0].price", is(payment.getPrice().doubleValue())))
                .andExpect(jsonPath("$[0].amount", is(payment.getAmount())))
                .andExpect(jsonPath("$[0].productId", is(payment.getProductId().toString())))
                .andExpect(jsonPath("$[0].userId", is(payment.getUserId().toString())));
    }

}
