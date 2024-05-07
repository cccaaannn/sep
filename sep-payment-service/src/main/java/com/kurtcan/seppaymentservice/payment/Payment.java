package com.kurtcan.seppaymentservice.payment;

import com.kurtcan.seppaymentservice.shared.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Document(collection = "payments")
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseEntity {
    private UUID userId;
    private UUID productId;
    private int amount;
    private BigDecimal price;
}