package com.kurtcan.sepproductservice.payment;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private UUID userId;
    private UUID productId;
    private int amount;
    private BigDecimal price;
}