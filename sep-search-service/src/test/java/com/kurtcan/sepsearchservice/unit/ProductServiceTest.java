package com.kurtcan.sepsearchservice.unit;

import com.kurtcan.sepsearchservice.product.Product;
import com.kurtcan.sepsearchservice.product.ProductElasticIndex;
import com.kurtcan.sepsearchservice.product.ProductService;
import com.kurtcan.sepsearchservice.product.ProductServiceImpl;
import com.kurtcan.sepsearchservice.shared.elasticsearch.SimpleElasticsearchClient;
import com.kurtcan.sepsearchservice.shared.elasticsearch.SimpleElasticsearchClientImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private final ProductService productService;

    private final SimpleElasticsearchClient elasticsearchClient;

    public ProductServiceTest() {
        elasticsearchClient = mock(SimpleElasticsearchClientImpl.class);
        this.productService = new ProductServiceImpl(elasticsearchClient);
    }

    @Test
    public void testSearchMultiFuzzyProduct() {
        // Given
        List<Product> products = List.of(
                new Product("Product 1", "Description 1", BigDecimal.ONE, 10),
                new Product("Product 2", "Description 2", BigDecimal.ONE, 10)
        );

        String name = "Product 1";
        String description = "Description 1";
        Map<String, String> searchMap = Map.of(
                "name", name,
                "description", description
        );

        // When
        when(elasticsearchClient.searchMultiFuzzy(ProductElasticIndex.NAME, searchMap, Product.class))
                .thenReturn(Optional.of(products));

        // Then
        List<Product> searchedProducts = productService.searchMultiFuzzy(name, description);

        // Verify
        verify(elasticsearchClient, times(1)).searchMultiFuzzy(ProductElasticIndex.NAME, searchMap, Product.class);
        Assertions.assertEquals(2, searchedProducts.size());
        Assertions.assertEquals(name, searchedProducts.getFirst().getName());
        Assertions.assertEquals(description, searchedProducts.getFirst().getDescription());
    }

    @Test
    public void testSearchMultiFuzzyProductEmpty() {
        // Given
        String name = "Product 1";

        // When
        when(elasticsearchClient.match(ProductElasticIndex.NAME, "name", name, Product.class))
                .thenReturn(Optional.empty());

        // Then
        List<Product> matchedProducts = productService.match(name);

        // Verify
        verify(elasticsearchClient, times(1)).match(ProductElasticIndex.NAME, "name", name, Product.class);
        Assertions.assertEquals(0, matchedProducts.size());
    }

    @Test
    public void testMatchProducts() {
        // Given
        List<Product> products = List.of(
                new Product("Product 1", "Description 1", BigDecimal.ONE, 10),
                new Product("Product 2", "Description 2", BigDecimal.ONE, 10)
        );

        String name = "Product 1";

        // When
        when(elasticsearchClient.match(ProductElasticIndex.NAME, "name", name, Product.class))
                .thenReturn(Optional.of(products));

        // Then
        List<Product> matchedProducts = productService.match(name);

        // Verify
        verify(elasticsearchClient, times(1)).match(ProductElasticIndex.NAME, "name", name, Product.class);
        Assertions.assertEquals(2, matchedProducts.size());
        Assertions.assertEquals(name, matchedProducts.getFirst().getName());
    }

    @Test
    public void testMatchProductsEmpty() {
        // Given
        String name = "Product 1";

        // When
        when(elasticsearchClient.match(ProductElasticIndex.NAME, "name", name, Product.class))
                .thenReturn(Optional.empty());

        // Then
        List<Product> matchedProducts = productService.match(name);

        // Verify
        verify(elasticsearchClient, times(1)).match(ProductElasticIndex.NAME, "name", name, Product.class);
        Assertions.assertEquals(0, matchedProducts.size());
    }

    @Test
    public void testSearchFuzzyProducts() {
        // Given
        List<Product> products = List.of(
                new Product("Product 1", "Description 1", BigDecimal.ONE, 10),
                new Product("Product 2", "Description 2", BigDecimal.ONE, 10)
        );

        String name = "Product 1";

        // When
        when(elasticsearchClient.searchFuzzy(ProductElasticIndex.NAME, "name", name, Product.class))
                .thenReturn(Optional.of(products));

        // Then
        List<Product> searchedProducts = productService.searchFuzzy(name);

        // Verify
        verify(elasticsearchClient, times(1)).searchFuzzy(ProductElasticIndex.NAME, "name", name, Product.class);
        Assertions.assertEquals(2, searchedProducts.size());
        Assertions.assertEquals(name, searchedProducts.getFirst().getName());
    }

    @Test
    public void testSearchFuzzyProductsEmpty() {
        // Given
        String name = "Product 1";

        // When
        when(elasticsearchClient.searchFuzzy(ProductElasticIndex.NAME, "name", name, Product.class))
                .thenReturn(Optional.empty());

        // Then
        List<Product> searchedProducts = productService.searchFuzzy(name);

        // Verify
        verify(elasticsearchClient, times(1)).searchFuzzy(ProductElasticIndex.NAME, "name", name, Product.class);
        Assertions.assertEquals(0, searchedProducts.size());
    }

}
