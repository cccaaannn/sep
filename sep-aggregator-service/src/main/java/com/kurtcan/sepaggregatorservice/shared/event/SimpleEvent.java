package com.kurtcan.sepaggregatorservice.shared.event;

import com.kurtcan.sepaggregatorservice.shared.entity.DbEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleEvent {
    private UUID id;

    public static SimpleEvent fromEntity(DbEntity dbEntity) {
        return SimpleEvent.builder()
                .id(dbEntity.getId())
                .build();
    }
}
