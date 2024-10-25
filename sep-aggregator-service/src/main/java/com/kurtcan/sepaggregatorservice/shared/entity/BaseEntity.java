package com.kurtcan.sepaggregatorservice.shared.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity implements DbEntity {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
}
