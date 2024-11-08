package com.kurtcan.sepaggregatorservice.integration;

import com.kurtcan.sepaggregatorservice.payment.Payment;
import com.kurtcan.sepaggregatorservice.payment.PaymentService;
import com.kurtcan.sepaggregatorservice.prodcut.Product;
import com.kurtcan.sepaggregatorservice.prodcut.ProductServiceClient;
import com.kurtcan.sepaggregatorservice.shared.constant.ProfileName;
import com.kurtcan.sepaggregatorservice.shared.jwt.TokenClient;
import com.kurtcan.sepaggregatorservice.shared.pagaination.PageImpl;
import com.kurtcan.sepaggregatorservice.shared.security.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles(ProfileName.TEST)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrentUserService currentUserService;

    @MockBean
    private TokenClient tokenClient;

    @MockBean
    private ProductServiceClient productServiceClient;

    @MockBean
    private PaymentService paymentService;

    @Test
    public void testGetProductWithPayments() throws Exception {
        // Mock the current user
        var userId = UUID.randomUUID();
        var currentUser = CurrentUserService.CurrentUser.builder().id(userId).build();
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));

        // Mock the token client
        var tokenResponse = new TokenClient.TokenResponse("token", "token", 1000);
        when(tokenClient.getTokenWithCircuitBreaker()).thenReturn(Optional.of(tokenResponse));

        // Mock the product service
        var productId = UUID.randomUUID();
        var productName = "Product 1";
        var product = Product.builder().id(productId).name(productName).build();
        var products = List.of(product);
        var productPage = PageImpl.<Product>builder().content(products).build();
        Mockito.when(productServiceClient.getAllProducts(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.of(productPage));

        // Mock the payment service
        var paymentId = UUID.randomUUID();
        var payment = Payment.builder().id(paymentId).userId(userId).productId(productId).build();
        var payments = List.of(payment);
        Mockito.when(paymentService.getUserPayments(userId))
                .thenReturn(payments);

        String query = "{" +
                "\"operationName\":\"Query\"," +
                "\"query\":\"query Query {  " +
                "products(order: \\\"\\\", page: 0, search: \\\"\\\", size: 10, sort: \\\"\\\") " +
                "{ content { createdAt description id name updatedAt stockAmount price payments { id } } }" +
                "}\"" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/graphql")
                        .contentType("application/json")
                        .content(query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.products.content").isArray())
                .andExpect(jsonPath("$.data.products.content[0].id").isNotEmpty())
                .andExpect(jsonPath("$.data.products.content[0].name").isNotEmpty())
                .andExpect(jsonPath("$.data.products.content[0].payments").isArray());
    }

}
