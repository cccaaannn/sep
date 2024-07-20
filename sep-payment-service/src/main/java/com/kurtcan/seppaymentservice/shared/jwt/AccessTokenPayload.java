package com.kurtcan.seppaymentservice.shared.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@Builder
public class AccessTokenPayload {
        @JsonProperty("sub")
        private UUID id;
        @EqualsAndHashCode.Exclude
        @JsonProperty("roles")
        private String[] roles;
}
