package com.kurtcan.seppaymentservice.product;

import com.kurtcan.seppaymentservice.shared.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Document(collection = "products")
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {
    private BigDecimal price;
    private int stockAmount;
}
