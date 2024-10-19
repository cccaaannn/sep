package com.kurtcan.seppaymentservice.shared.event;

import com.kurtcan.seppaymentservice.shared.entity.DbEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataEvent<T> {
    private UUID id;
    private T data;

    public static <T> DataEvent<T> fromEntity(DbEntity dbEntity, T data) {
        return DataEvent.<T>builder()
                .id(dbEntity.getId())
                .data(data)
                .build();
    }
}
