package com.kurtcan.sepsearchservice.product;

import com.kurtcan.sepsearchservice.shared.constant.ServiceName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@FeignClient(ServiceName.PRODUCT)
public interface ProductServiceClient {
    @GetMapping("/products/{productId}")
    Optional<Product> getProductById(@PathVariable("productId") UUID productId);
}
