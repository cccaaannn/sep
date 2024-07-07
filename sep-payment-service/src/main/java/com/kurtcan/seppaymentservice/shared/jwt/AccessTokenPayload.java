package com.kurtcan.seppaymentservice.shared.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AccessTokenPayload(
        @JsonProperty("sub") UUID id,
        @JsonProperty("roles") String[] roles
) {
}
