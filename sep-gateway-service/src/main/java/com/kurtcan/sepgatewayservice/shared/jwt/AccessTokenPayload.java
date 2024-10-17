package com.kurtcan.sepgatewayservice.shared.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenPayload {
	@JsonProperty("sub")
	private UUID id;
	@EqualsAndHashCode.Exclude
	@JsonProperty("roles")
	private String[] roles;
}
