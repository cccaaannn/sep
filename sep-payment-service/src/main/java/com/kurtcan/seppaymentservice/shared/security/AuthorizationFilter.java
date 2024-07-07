package com.kurtcan.seppaymentservice.shared.security;

import com.kurtcan.seppaymentservice.shared.constant.ProfileName;
import com.kurtcan.seppaymentservice.shared.jwt.AccessTokenPayload;
import com.kurtcan.seppaymentservice.shared.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Profile("!" + ProfileName.TEST)
public class AuthorizationFilter implements WebFilter {

    private final JwtUtils jwtUtils;
    private final SecurityGlobalProperties securityGlobalProperties;
    private final CurrentUserService currentUserService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

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

    private boolean isSecured(ServerHttpRequest request) {
        return securityGlobalProperties
                .getWhiteListPaths()
                .stream()
                .noneMatch(uri -> pathMatcher.match(uri, request.getURI().getPath()));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (!isSecured(request)) return chain.filter(exchange);

        Optional<String> tokenOptional = getBearerToken(request);

        if (tokenOptional.isEmpty()) return onError(exchange, HttpStatus.UNAUTHORIZED);

        Optional<AccessTokenPayload> tokenPayload = jwtUtils.decodeToken(tokenOptional.get(), AccessTokenPayload.class);

        if (tokenPayload.isEmpty()) return this.onError(exchange, HttpStatus.FORBIDDEN);

        currentUserService.fromAccessTokenPayload(exchange, tokenPayload.get());

        return chain.filter(exchange);
    }
}