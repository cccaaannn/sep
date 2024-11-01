package com.kurtcan.sepaggregatorservice.prodcut;

import com.kurtcan.sepaggregatorservice.payment.Payment;
import com.kurtcan.sepaggregatorservice.shared.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ProductWithPayments extends BaseEntity {
    private String name;
    private String description;
    private BigDecimal price;
    private int stockAmount;
    @Builder.Default
    private List<Payment> payments = List.of();
}
