package com.kurtcan.seppaymentservice.shared.entity;

import java.time.Instant;
import java.util.UUID;

public interface DbEntity {
    UUID getId();
    Instant getCreatedAt();
    Instant getUpdatedAt();
}
