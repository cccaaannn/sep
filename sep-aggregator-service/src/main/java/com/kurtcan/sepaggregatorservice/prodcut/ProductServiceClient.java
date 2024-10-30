package com.kurtcan.sepaggregatorservice.prodcut;

import com.kurtcan.sepaggregatorservice.shared.constant.ServiceName;
import com.kurtcan.sepaggregatorservice.shared.pagaination.PageImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.UUID;

@FeignClient(ServiceName.PRODUCT)
public interface ProductServiceClient {
    @GetMapping("/products")
    Optional<PageImpl<Product>> getAllProducts(
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestHeader("Authorization") String bearerToken
    );

    @GetMapping("/products/{productId}")
    Optional<Product> getProductById(
            @PathVariable("productId") UUID productId,
            @RequestHeader("Authorization") String bearerToken
    );
}

