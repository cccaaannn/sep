package com.kurtcan.seppaymentservice.product;

import com.kurtcan.seppaymentservice.shared.constant.ServiceName;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductServiceClient {

    private final DiscoveryClient discoveryClient;

    private WebClient webClient() {
        List<ServiceInstance> instances = discoveryClient.getInstances(ServiceName.PRODUCT);
        if (instances.isEmpty()) {
            throw new RuntimeException(MessageFormat.format("No instances of {0} found", ServiceName.PRODUCT));
        }
        URI uri = instances.getFirst().getUri();
        return WebClient.create(uri.toString());
    }

    public Mono<Product> getProduct(UUID id, String token) {
        WebClient webClient = webClient();
        return webClient.get()
                .uri("/products/{id}", id)
                .header("Authorization", MessageFormat.format("Bearer {0}", token))
                .retrieve()
                .bodyToMono(Product.class);
    }

}
