package com.kurtcan.sepsearchservice.shared.jwt;

import java.util.Optional;

public interface JwtUtils {
    <T> Optional<T> decodeToken(String token, Class<T> clazz);
}
