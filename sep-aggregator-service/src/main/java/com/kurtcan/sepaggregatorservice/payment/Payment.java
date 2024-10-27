package com.kurtcan.sepaggregatorservice.payment;

import com.kurtcan.sepaggregatorservice.shared.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseEntity {
    private UUID userId;
    private UUID productId;
    private int amount;
    private BigDecimal price;
}