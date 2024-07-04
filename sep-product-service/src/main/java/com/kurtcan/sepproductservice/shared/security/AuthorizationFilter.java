package com.kurtcan.sepproductservice.shared.security;

import com.kurtcan.sepproductservice.shared.jwt.JwtUtils;
import com.kurtcan.sepproductservice.shared.jwt.AccessTokenPayload;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class AuthorizationFilter implements Filter {

    private final JwtUtils jwtUtils;
    private final SecurityGlobalProperties securityGlobalProperties;
    private final CurrentUserService currentUserService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private Optional<String> getBearerToken(HttpServletRequest request) {
        List<String> tokenList = Collections.list(request.getHeaders("Authorization"));
        if (tokenList.size() != 1) return Optional.empty();

        String[] tokenSplit = tokenList.getFirst().split(" ");
        if (tokenSplit.length != 2 || !tokenSplit[0].equals("Bearer")) return Optional.empty();

        String token = tokenSplit[1];
        return token.isBlank() ? Optional.empty() : Optional.of(token);
    }

    private boolean isSecured(HttpServletRequest request) {
        return securityGlobalProperties.getWhiteListPaths().stream()
                .noneMatch(uri -> pathMatcher.match(uri, request.getServletPath()));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!isSecured(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> tokenOptional = getBearerToken(request);

        if (tokenOptional.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        Optional<AccessTokenPayload> tokenPayload = jwtUtils.decodeToken(tokenOptional.get(), AccessTokenPayload.class);

        if (tokenPayload.isEmpty()) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        currentUserService.fromAccessTokenPayload(tokenPayload.get());

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}