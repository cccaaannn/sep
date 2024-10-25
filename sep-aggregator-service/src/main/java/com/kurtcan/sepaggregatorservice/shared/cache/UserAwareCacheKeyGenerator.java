package com.kurtcan.sepaggregatorservice.shared.cache;

import com.kurtcan.sepaggregatorservice.shared.security.CurrentUserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.StringJoiner;

@RequiredArgsConstructor
@Component("userAwareCacheKeyGenerator")
public class UserAwareCacheKeyGenerator implements KeyGenerator {

    private final CurrentUserService currentUserService;

    @NonNull
    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringJoiner key = new StringJoiner(":");

        // Add the current user id if present
        currentUserService.getCurrentUser().ifPresent(user -> key.add(user.getId().toString()));

        key.add(target.getClass().getSimpleName()); // Add the bean name
        key.add(method.getName()); // Add the method name
        for (Object param : params) {
            key.add(param.toString()); // Add the method parameters
        }
        return key.toString();
    }
}
