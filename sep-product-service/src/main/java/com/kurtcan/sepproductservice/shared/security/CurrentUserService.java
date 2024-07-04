package com.kurtcan.sepproductservice.shared.security;

import com.kurtcan.sepproductservice.shared.jwt.AccessTokenPayload;
import lombok.Builder;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.UUID;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CurrentUserService {

    @Builder
    public record CurrentUser(UUID id, String[] roles) {
    }

    private CurrentUser currentUser;

    public void fromAccessTokenPayload(AccessTokenPayload accessTokenPayload) {
        currentUser = CurrentUser.builder().id(accessTokenPayload.id()).roles(accessTokenPayload.roles()).build();
    }

    public Optional<CurrentUser> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

}
