package com.kurtcan.sepsearchservice.integration;

import com.kurtcan.sepsearchservice.initialiser.ElasticsearchTestContainerInitializer;
import com.kurtcan.sepsearchservice.product.Product;
import com.kurtcan.sepsearchservice.product.ProductElasticIndex;
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
public class ProductSearchIntegrationTest extends ElasticsearchTestContainerInitializer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SimpleElasticsearchClient elasticsearchClient;

    @AfterEach
    void tearDown() {
        Optional<Response> deleteResult = elasticsearchClient.deleteIndex(ProductElasticIndex.NAME);
        if (deleteResult.isEmpty() || deleteResult.get().getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed to delete index");
        }
    }

    @Test
    public void testFuzzySearchProduct() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        String name = "Product 1";
        String description = "Description 1";
        BigDecimal price = BigDecimal.TEN;
        int stockAmount = 10;

        Product product = new Product(name, description, price, stockAmount);
        product.setId(id);
        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());

        // Index the product
        Optional<Response> saveResponse = elasticsearchClient.save(ProductElasticIndex.NAME, product);
        Assertions.assertNotNull(saveResponse.get());
        Assertions.assertEquals(201, saveResponse.get().getStatusLine().getStatusCode());

        // Elasticsearch needs some time before querying it back
        Thread.sleep(2000);

        // Then - Verify
        mockMvc.perform(MockMvcRequestBuilders.get("/search/products/fuzzy")
                        .param("name", name)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(product.getId().toString())));
    }

    @Test
    public void testMatchSearchProduct() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        String name = "Product 1";
        String description = "Description 1";
        BigDecimal price = BigDecimal.TEN;
        int stockAmount = 10;

        Product product = new Product(name, description, price, stockAmount);
        product.setId(id);
        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());

        // Index the product
        Optional<Response> saveResponse = elasticsearchClient.save(ProductElasticIndex.NAME, product);
        Assertions.assertNotNull(saveResponse.get());
        Assertions.assertEquals(201, saveResponse.get().getStatusLine().getStatusCode());

        // Elasticsearch needs some time before querying it back
        Thread.sleep(2000);

        // Then - Verify
        mockMvc.perform(MockMvcRequestBuilders.get("/search/products/match")
                        .param("name", name)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(product.getId().toString())));
    }

}
