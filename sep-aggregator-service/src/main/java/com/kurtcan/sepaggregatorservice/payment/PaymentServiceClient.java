package com.kurtcan.sepaggregatorservice.payment;

import com.kurtcan.sepaggregatorservice.shared.constant.ServiceName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@FeignClient(ServiceName.PAYMENT)
public interface PaymentServiceClient {
    @GetMapping("/payments/user/{userId}")
    Optional<List<Payment>> getByUserId(
            @PathVariable("userId") UUID userId,
            @RequestHeader("Authorization") String bearerToken
    );
}

