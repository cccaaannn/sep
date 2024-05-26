package com.kurtcan.sepproductservice.unit.product;

import com.kurtcan.sepproductservice.product.*;
import com.kurtcan.sepproductservice.product.request.ProductAdd;
import com.kurtcan.sepproductservice.product.request.ProductUpdate;
import com.kurtcan.sepproductservice.shared.mapper.ModelMapperConfig;
import com.kurtcan.sepproductservice.shared.event.JsonEventPublisher;
import com.kurtcan.sepproductservice.shared.event.SimpleEvent;
import com.kurtcan.sepproductservice.shared.pagaination.PageImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private final ArgumentCaptor<Product> productCaptor;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final JsonEventPublisher eventPublisher;

    public ProductServiceTest() {
        productRepository = mock(ProductRepository.class);
        eventPublisher = mock(JsonEventPublisher.class);

        productCaptor = ArgumentCaptor.forClass(Product.class);

        ModelMapperConfig modelMapperConfig = new ModelMapperConfig();

        productService = ProductServiceImpl.builder().
                repository(productRepository)
                .eventPublisher(eventPublisher)
                .mapper(modelMapperConfig.getModelMapper())
                .build();
    }

    @Test
    public void testAddProduct() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "Product 1";
        String description = "Description 1";
        BigDecimal price = BigDecimal.TEN;
        int stockAmount = 10;

        Product product = Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .stockAmount(stockAmount)
                .build();

        ProductAdd productAdd = ProductAdd.builder()
                .name(name)
                .description(description)
                .price(price)
                .stockAmount(stockAmount)
                .build();

        // When
        when(productRepository.save(productCaptor.capture())).thenReturn(product);

        // Then
        Product createdProduct = productService.addProduct(productAdd);

        // Verify
        verify(productRepository, times(1)).save(productCaptor.getValue());
        verify(eventPublisher, times(1)).publish(ProductEventTopic.CREATED, SimpleEvent.fromEntity(product));
        Assertions.assertEquals(product.getId(), createdProduct.getId());
        Assertions.assertEquals(product.getName(), createdProduct.getName());
        Assertions.assertEquals(product.getDescription(), createdProduct.getDescription());
        Assertions.assertEquals(product.getPrice(), createdProduct.getPrice());
    }

    @Test
    public void testUpdateProduct() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "Product 1";
        String description = "Description 1";
        BigDecimal price = BigDecimal.TEN;
        int stockAmount = 10;

        Product product = Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .stockAmount(stockAmount)
                .build();

        ProductUpdate productUpdate = ProductUpdate.builder()
                .name(name)
                .description(description)
                .price(price)
                .stockAmount(stockAmount)
                .build();

        // When
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(productCaptor.capture())).thenReturn(product);

        // Then
        Product updatedProduct = productService.updateProduct(id, productUpdate);

        // Verify
        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).save(productCaptor.getValue());
        verify(eventPublisher, times(1)).publish(ProductEventTopic.UPDATED, SimpleEvent.fromEntity(product));
        Assertions.assertEquals(product.getId(), updatedProduct.getId());
        Assertions.assertEquals(product.getName(), updatedProduct.getName());
        Assertions.assertEquals(product.getDescription(), updatedProduct.getDescription());
        Assertions.assertEquals(product.getPrice(), updatedProduct.getPrice());
    }

    @Test
    public void testUpdateProductNotFound() {
        // Given
        UUID id = UUID.randomUUID();
        ProductUpdate productUpdate = ProductUpdate.builder()
                .name("Product 1")
                .description("Description 1")
                .price(BigDecimal.TEN)
                .stockAmount(10)
                .build();

        // When
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(RuntimeException.class, () -> productService.updateProduct(id, productUpdate));

        // Verify
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    public void testDeleteProduct() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "Product 1";
        String description = "Description 1";
        BigDecimal price = BigDecimal.TEN;
        int stockAmount = 10;

        Product product = Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .stockAmount(stockAmount)
                .build();

        // When
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // Then
        productService.deleteProduct(id);

        // Verify
        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).deleteById(id);
        verify(eventPublisher, times(1)).publish(ProductEventTopic.DELETED, SimpleEvent.fromEntity(product));
    }

    @Test
    public void testDeleteProductNotFound() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(RuntimeException.class, () -> productService.deleteProduct(id));

        // Verify
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    public void testGetProduct() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "Product 1";
        String description = "Description 1";
        BigDecimal price = BigDecimal.TEN;
        int stockAmount = 10;

        Product product = Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .stockAmount(stockAmount)
                .build();

        // When
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // Then
        Product foundProduct = productService.getProduct(id);

        // Verify
        verify(productRepository, times(1)).findById(id);
        Assertions.assertEquals(product.getId(), foundProduct.getId());
        Assertions.assertEquals(product.getName(), foundProduct.getName());
        Assertions.assertEquals(product.getDescription(), foundProduct.getDescription());
        Assertions.assertEquals(product.getPrice(), foundProduct.getPrice());
    }

    @Test
    public void testGetProductNotFound() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(RuntimeException.class, () -> productService.getProduct(id));

        // Verify
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    public void testSearchProductWithoutSearchQuery() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "Product 1";
        String description = "Description 1";
        BigDecimal price = BigDecimal.TEN;
        int stockAmount = 10;

        Product product = Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .stockAmount(stockAmount)
                .build();

        List<Product> productList = List.of(product);
        Page<Product> productPage = new org.springframework.data.domain.PageImpl<>(productList);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        when(productRepository.findAll(pageRequest)).thenReturn(productPage);

        // Then
        PageImpl<Product> fetchedProducts = productService.searchProduct("", pageRequest);
        Product fetchedProduct = fetchedProducts.content().getFirst();

        // Verify
        verify(productRepository, times(1)).findAll(pageRequest);
        Assertions.assertEquals(fetchedProducts.size(), productPage.getSize());
        Assertions.assertEquals(fetchedProduct.getId(), product.getId());
        Assertions.assertEquals(fetchedProduct.getName(), product.getName());
        Assertions.assertEquals(fetchedProduct.getDescription(), product.getDescription());
        Assertions.assertEquals(fetchedProduct.getPrice(), product.getPrice());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSearchProductWithSearchQuery() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "Product 1";
        String description = "Description 1";
        BigDecimal price = BigDecimal.TEN;
        int stockAmount = 10;

        String searchQuery = "name:Product 1";

        Product product = Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .stockAmount(stockAmount)
                .build();

        List<Product> productList = List.of(product);
        Page<Product> productPage = new org.springframework.data.domain.PageImpl<>(productList);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        when(productRepository.findAll((Specification<Product>) any(), eq(pageRequest))).thenReturn(productPage);

        // Then
        PageImpl<Product> fetchedProducts = productService.searchProduct(searchQuery, pageRequest);
        Product fetchedProduct = fetchedProducts.content().getFirst();

        // Verify
        verify(productRepository, times(1)).findAll((Specification<Product>) any(), eq(pageRequest));
        Assertions.assertEquals(fetchedProducts.size(), productPage.getSize());
        Assertions.assertEquals(fetchedProduct.getId(), product.getId());
        Assertions.assertEquals(fetchedProduct.getName(), product.getName());
        Assertions.assertEquals(fetchedProduct.getDescription(), product.getDescription());
        Assertions.assertEquals(fetchedProduct.getPrice(), product.getPrice());
    }

}
