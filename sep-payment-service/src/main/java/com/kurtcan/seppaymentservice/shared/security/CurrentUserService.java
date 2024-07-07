package com.kurtcan.seppaymentservice.shared.security;

import com.kurtcan.seppaymentservice.shared.jwt.AccessTokenPayload;
import lombok.Builder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CurrentUserService {

    private final String CURRENT_USER = "currentUser";

    @Builder
    public record CurrentUser(UUID id, String[] roles) {
    }

    public void fromAccessTokenPayload(ServerWebExchange exchange, AccessTokenPayload accessTokenPayload) {
        CurrentUser currentUser = CurrentUser.builder().id(accessTokenPayload.id()).roles(accessTokenPayload.roles()).build();
        exchange.getAttributes().put(CURRENT_USER, currentUser);
    }

    public Mono<CurrentUser> getCurrentUser(ServerWebExchange exchange) {
        return Mono.justOrEmpty((CurrentUser) exchange.getAttributes().get(CURRENT_USER));
    }
}
