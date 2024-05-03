package com.kurtcan.sepproductservice.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductUpdate(
        @NotBlank
        @Size(
                min = 3,
                max = 250,
                message = "must be between 3 and 250 characters"
        )
        String name,
        @NotBlank
        @Size(
                min = 3,
                max = 1000,
                message = "must be between 3 and 1000 characters"
        )
        String description,
        @PositiveOrZero
        BigDecimal price,
        @PositiveOrZero
        int stockAmount
) {}
