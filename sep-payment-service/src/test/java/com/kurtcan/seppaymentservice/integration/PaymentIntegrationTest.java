package com.kurtcan.seppaymentservice.integration;

import com.jayway.jsonpath.JsonPath;
import com.kurtcan.seppaymentservice.initialiser.MongodbTestContainerInitializer;
import com.kurtcan.seppaymentservice.payment.Payment;
import com.kurtcan.seppaymentservice.payment.PaymentRepository;
import com.kurtcan.seppaymentservice.payment.request.PaymentCreate;
import com.kurtcan.seppaymentservice.product.Product;
import com.kurtcan.seppaymentservice.product.ProductRepository;
import com.kurtcan.seppaymentservice.shared.constant.ProfileName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles(ProfileName.TEST)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}
)
public class PaymentIntegrationTest extends MongodbTestContainerInitializer {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void clearDatabase() {
        productRepository.deleteAll().block();
        paymentRepository.deleteAll().block();
    }

    @Test
    public void testCreatePayment() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int amount = 10;
        BigDecimal price = BigDecimal.valueOf(100);
        int stockAmount = 100;

        Product product = productRepository.save(
                Product.builder()
                        .id(productId)
                        .stockAmount(stockAmount)
                        .price(price)
                        .build()
        ).block();
        assert product != null;

        PaymentCreate paymentCreate = PaymentCreate.builder()
                .userId(userId)
                .productId(productId)
                .amount(amount)
                .build();

        // Then - Verify
        var apiResponse = webTestClient.post()
                .uri("/payments")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(paymentCreate), PaymentCreate.class)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(String.class)
                .getResponseBody().
                blockFirst();

        var apiResponsePared = JsonPath.parse(apiResponse);

        UUID createdId = UUID.fromString(apiResponsePared.read("$.id", String.class));

        Payment dbPayment = paymentRepository.findById(createdId).block();
        assert dbPayment != null;

        Assertions.assertEquals(apiResponsePared.read("$.id", String.class), dbPayment.getId().toString());
        Assertions.assertEquals(apiResponsePared.read("$.userId", String.class), dbPayment.getUserId().toString());
        Assertions.assertEquals(apiResponsePared.read("$.productId", String.class), dbPayment.getProductId().toString());
        Assertions.assertEquals(apiResponsePared.read("$.amount", Integer.class), dbPayment.getAmount());
        Assertions.assertEquals(apiResponsePared.read("$.price", BigDecimal.class), dbPayment.getPrice());

        Product dbProduct = productRepository.findById(productId).block();
        assert dbProduct != null;

        Assertions.assertEquals(dbProduct.getStockAmount(), stockAmount - amount);
    }

    @Test
    public void testCreatePaymentProductNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int amount = 10;

        PaymentCreate paymentCreate = PaymentCreate.builder()
                .userId(userId)
                .productId(productId)
                .amount(amount)
                .build();

        // Then - Verify
        webTestClient.post()
                .uri("/payments")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(paymentCreate), PaymentCreate.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").exists();
    }

    @Test
    public void testCreatePaymentNotEnoughStock() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int amount = 10;
        BigDecimal price = BigDecimal.valueOf(100);
        int stockAmount = 5;

        Product product = productRepository.save(
                Product.builder()
                        .id(productId)
                        .stockAmount(stockAmount)
                        .price(price)
                        .build()
        ).block();
        assert product != null;

        PaymentCreate paymentCreate = PaymentCreate.builder()
                .userId(userId)
                .productId(productId)
                .amount(amount)
                .build();

        // Then - Verify
        webTestClient.post()
                .uri("/payments")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(paymentCreate), PaymentCreate.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").exists();
    }

    @Test
    public void testGetAllPayments() {
        // Given
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int amount = 10;
        BigDecimal price = BigDecimal.valueOf(100);

        Payment payment = paymentRepository.save(
                Payment.builder()
                        .id(id)
                        .userId(userId)
                        .productId(productId)
                        .amount(amount)
                        .price(price)
                        .build()
        ).block();
        assert payment != null;

        // Then - Verify
        webTestClient.get()
                .uri("/payments")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(payment.getId().toString())
                .jsonPath("$[0].userId").isEqualTo(payment.getUserId().toString())
                .jsonPath("$[0].productId").isEqualTo(payment.getProductId().toString())
                .jsonPath("$[0].amount").isEqualTo(payment.getAmount())
                .jsonPath("$[0].price").isEqualTo(payment.getPrice());
    }

    @Test
    public void testGetPaymentById() {
        // Given
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int amount = 10;
        BigDecimal price = BigDecimal.valueOf(100);

        Payment payment = paymentRepository.save(
                Payment.builder()
                        .id(id)
                        .userId(userId)
                        .productId(productId)
                        .amount(amount)
                        .price(price)
                        .build()
        ).block();
        assert payment != null;

        // Then - Verify
        webTestClient.get()
                .uri(STR."/payments/\{id.toString()}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(payment.getId().toString())
                .jsonPath("$.userId").isEqualTo(payment.getUserId().toString())
                .jsonPath("$.productId").isEqualTo(payment.getProductId().toString())
                .jsonPath("$.amount").isEqualTo(payment.getAmount())
                .jsonPath("$.price").isEqualTo(payment.getPrice());
    }

    @Test
    public void testGetPaymentByIdNotFound() {
        // Given
        UUID id = UUID.randomUUID();

        // Then - Verify
        webTestClient.get()
                .uri(STR."/payments/\{id.toString()}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").exists();
    }

}