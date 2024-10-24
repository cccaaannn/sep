package com.kurtcan.sepaggregatorservice.shared.jwt;

import com.kurtcan.sepaggregatorservice.shared.constant.ServiceName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(
        value = ServiceName.KEYCLOAK,
        url = "${jwt-global.token-endpoint}"
)
public interface KeycloakClient {
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenClient.TokenResponse getToken(Map<String, ?> data);
}
