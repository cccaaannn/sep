package com.kurtcan.sepgatewayservice.shared.security;

import com.kurtcan.sepgatewayservice.shared.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
@RefreshScope
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "authentication-filter", name = "enabled")
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final SecurityGlobalProperties securityGlobalProperties;
    private final JwtUtils jwtUtils;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (!isSecured(request)) return chain.filter(exchange);

        Optional<String> tokenOptional = getBearerToken(request);

        if (tokenOptional.isEmpty()) return onError(exchange, HttpStatus.UNAUTHORIZED);

        if (!isTokenValid(tokenOptional.get())) return this.onError(exchange, HttpStatus.FORBIDDEN);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isSecured(ServerHttpRequest request) {
        return securityGlobalProperties
                .getWhiteListPaths()
                .stream()
                .noneMatch(uri -> pathMatcher.match(uri, request.getURI().getPath()));
    }

    private boolean isTokenValid(String token) {
        return jwtUtils.isTokenValid(token);
    }

    private Optional<String> getBearerToken(ServerHttpRequest request) {
        List<String> tokenList = request.getHeaders().getOrEmpty("Authorization");
        if (tokenList.size() != 1) return Optional.empty();

        String[] tokenSplit = tokenList.getFirst().split(" ");
        if (tokenSplit.length != 2 || !tokenSplit[0].equals("Bearer")) return Optional.empty();

        String token = tokenSplit[1];
        return token.isBlank() ? Optional.empty() : Optional.of(token);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

}