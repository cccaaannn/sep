package com.kurtcan.seppaymentservice.shared.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kurtcan.seppaymentservice.shared.circuitbreaker.CircuitBreakerName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenClient {

    public record TokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("token_type") String tokenType,
            @JsonProperty("expires_in") int expiresIn) {
    }

    private final JwtGlobalProperties jwtGlobalProperties;
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;
    @Qualifier(CircuitBreakerName.TOKEN)
    private final CircuitBreaker tokenClientCircuitBreaker;

    public Mono<TokenResponse> getTokenWithCircuitBreaker() {
        return tokenClientCircuitBreaker.run(this::getToken, throwable -> {
            log.error("Error while getting token: {}", throwable.getMessage());
            return Mono.empty();
        });
    }

    public Mono<TokenResponse> getToken() {
        final WebClient webClient = WebClient.create(jwtGlobalProperties.getTokenEndpoint());
        return webClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(MessageFormat.format("grant_type=client_credentials&client_id={0}&client_secret={1}",
                        jwtProperties.getClientId(), jwtProperties.getClientSecret()))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("Raw token response: {}", response))
                .flatMap(response -> {
                    try {
                        return Mono.just(objectMapper.readValue(response, TokenResponse.class));
                    } catch (JsonProcessingException e) {
                        log.info("Error while parsing token response: {}", e.getMessage());
                        return Mono.empty();
                    }
                });
    }

}