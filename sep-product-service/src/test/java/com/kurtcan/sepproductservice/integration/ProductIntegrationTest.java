package com.kurtcan.sepproductservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kurtcan.sepproductservice.product.Product;
import com.kurtcan.sepproductservice.product.ProductRepository;
import com.kurtcan.sepproductservice.product.request.ProductAdd;
import com.kurtcan.sepproductservice.product.request.ProductUpdate;
import com.kurtcan.sepproductservice.shared.constant.ProfileName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles(ProfileName.TEST)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}
)
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void clearDatabase() {
        productRepository.deleteAll();
    }

    @Test
    public void testGetProducts() throws Exception {
        // Given
        Product product = Product.builder()
                .name("Product 1")
                .description("Description 1")
                .price(BigDecimal.valueOf(100))
                .stockAmount(10)
                .build();

        product = productRepository.save(product);

        // Then - Verify
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .param("search", "")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt")
                        .param("order", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(product.getId().toString())))
                .andExpect(jsonPath("$.content[0].name", is(product.getName())))
                .andExpect(jsonPath("$.content[0].description", is(product.getDescription())))
                .andExpect(jsonPath("$.content[0].price", is(product.getPrice().doubleValue())));
    }

    @Test
    public void testGetProduct() throws Exception {
        // Given
        Product product = Product.builder()
                .name("Product 1")
                .description("Description 1")
                .price(BigDecimal.valueOf(100))
                .stockAmount(10)
                .build();

        product = productRepository.save(product);

        // Then - Verify
        mockMvc.perform(MockMvcRequestBuilders.get(STR."/products/\{product.getId()}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product.getId().toString())))
                .andExpect(jsonPath("$.name", is(product.getName())))
                .andExpect(jsonPath("$.description", is(product.getDescription())))
                .andExpect(jsonPath("$.price", is(product.getPrice().doubleValue())));
    }

    @Test
    public void testGetProductNotFound() throws Exception {
        // Given
        String id = "123e4567-e89b-12d3-a456-556642440000";

        // Then - Verify
        mockMvc.perform(MockMvcRequestBuilders.get(STR."/products/\{id}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testAddProduct() throws Exception {
        // Given
        ProductAdd productAdd = ProductAdd.builder()
                .name("Product 1")
                .description("Description 1")
                .price(BigDecimal.valueOf(100.5))
                .stockAmount(10)
                .build();

        // Then - Verify
        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productAdd)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(productAdd.name())))
                .andExpect(jsonPath("$.description", is(productAdd.description())))
                .andExpect(jsonPath("$.price", is(productAdd.price().doubleValue())));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        // Given
        Product product = Product.builder()
                .name("Product 1")
                .description("Description 1")
                .price(BigDecimal.valueOf(100))
                .stockAmount(10)
                .build();

        product = productRepository.save(product);

        ProductUpdate productUpdate = ProductUpdate.builder()
                .name("Product 2")
                .description("Description 2")
                .price(BigDecimal.valueOf(200.5))
                .stockAmount(11)
                .build();

        // Then - Verify
        mockMvc.perform(MockMvcRequestBuilders.put(STR."/products/\{product.getId()}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product.getId().toString())))
                .andExpect(jsonPath("$.name", is(productUpdate.name())))
                .andExpect(jsonPath("$.description", is(productUpdate.description())))
                .andExpect(jsonPath("$.price", is(productUpdate.price().doubleValue())));
    }

    @Test
    public void testUpdateProductNotFound() throws Exception {
        // Given
        String id = "123e4567-e89b-12d3-a456-556642440000";

        ProductUpdate productUpdate = ProductUpdate.builder()
                .name("Product 1")
                .description("Description 1")
                .price(BigDecimal.valueOf(100.5))
                .stockAmount(10)
                .build();

        // Then - Verify
        mockMvc.perform(MockMvcRequestBuilders.put(STR."/products/\{id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdate)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testDeleteProduct() throws Exception {
        // Given
        Product product = Product.builder()
                .name("Product 1")
                .description("Description 1")
                .price(BigDecimal.valueOf(100))
                .stockAmount(10)
                .build();

        product = productRepository.save(product);

        // Then - Verify
        mockMvc.perform(MockMvcRequestBuilders.delete(STR."/products/\{product.getId()}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testDeleteProductNotFound() throws Exception {
        // Given
        String id = "123e4567-e89b-12d3-a456-556642440000";

        // Then - Verify
        mockMvc.perform(MockMvcRequestBuilders.delete(STR."/products/\{id}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

}
