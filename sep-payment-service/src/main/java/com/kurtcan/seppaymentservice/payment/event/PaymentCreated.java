package com.kurtcan.seppaymentservice.payment.event;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentCreated(
        @NotNull
        UUID userId,
        @NotNull
        UUID productId
) {
}