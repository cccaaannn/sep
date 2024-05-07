package com.kurtcan.seppaymentservice.payment.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentCreate(
        @NotNull
        UUID userId,
        @NotNull
        UUID productId,
        @Positive
        int amount
) {
}