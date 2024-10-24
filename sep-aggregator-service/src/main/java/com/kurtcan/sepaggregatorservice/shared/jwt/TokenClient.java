package com.kurtcan.sepaggregatorservice.shared.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kurtcan.sepaggregatorservice.shared.circuitbreaker.CircuitBreakerName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenClient {

    public record TokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("token_type") String tokenType,
            @JsonProperty("expires_in") int expiresIn
    ) {
    }

    private final JwtProperties jwtProperties;
    private final KeycloakClient keycloakClient;
    @Qualifier(CircuitBreakerName.TOKEN)
    private final CircuitBreaker tokenClientCircuitBreaker;

    public Optional<TokenResponse> getTokenWithCircuitBreaker() {
        return tokenClientCircuitBreaker.run(this::getToken, throwable -> {
            log.error("Error while getting token: {}", throwable.getMessage());
            return Optional.empty();
        });
    }

    public Optional<TokenResponse> getToken() {

        Map<String, String> body = new HashMap<>();
        body.put("client_id", jwtProperties.getClientId());
        body.put("client_secret", jwtProperties.getClientSecret());
        body.put("grant_type", "client_credentials");

        try {
            var token = keycloakClient.getToken(body);
            return Optional.of(token);
        } catch (Exception e) {
            log.error("Error while getting token: {}", e.getMessage());
            return Optional.empty();
        }
    }

}
