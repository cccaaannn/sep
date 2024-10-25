package com.kurtcan.sepaggregatorservice.shared.graphql;

import com.kurtcan.sepaggregatorservice.shared.constant.ProfileName;
import com.kurtcan.sepaggregatorservice.shared.exception.QueryException;
import com.kurtcan.sepaggregatorservice.shared.jwt.AccessTokenPayload;
import com.kurtcan.sepaggregatorservice.shared.jwt.JwtUtils;
import com.kurtcan.sepaggregatorservice.shared.security.CurrentUserService;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Profile("!" + ProfileName.TEST)
public class GraphQLInterceptor implements WebGraphQlInterceptor {

    private final JwtUtils jwtUtils;
    private final CurrentUserService currentUserService;

    private static final String INTROSPECTION_QUERY = "IntrospectionQuery";

    private Optional<String> getBearerToken(WebGraphQlRequest request) {
        List<String> tokenList = request.getHeaders().get("Authorization");
        if (Objects.isNull(tokenList) || tokenList.size() != 1) return Optional.empty();

        String[] tokenSplit = tokenList.getFirst().split(" ");
        if (tokenSplit.length != 2 || !tokenSplit[0].equals("Bearer")) return Optional.empty();

        String token = tokenSplit[1];
        return token.isBlank() ? Optional.empty() : Optional.of(token);
    }

    @NonNull
    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, @NonNull Chain chain) {
        String operationName = request.getOperationName();

        if (Objects.isNull(operationName)) {
            return chain.next(request).map(response -> buildExceptionResponse(response, new QueryException("Operation name cannot be null", ErrorType.UNAUTHORIZED)));
        }

        if (operationName.equalsIgnoreCase(INTROSPECTION_QUERY)) {
            return chain.next(request);
        }

        Optional<String> tokenOptional = getBearerToken(request);

        if (tokenOptional.isEmpty()) {
            return chain.next(request).map(response -> buildExceptionResponse(response, new QueryException("Cannot get token from request", ErrorType.UNAUTHORIZED)));
        }

        Optional<AccessTokenPayload> tokenPayload = jwtUtils.decodeToken(tokenOptional.get(), AccessTokenPayload.class);

        if (tokenPayload.isEmpty()) {
            return chain.next(request).map(response -> buildExceptionResponse(response, new QueryException("Token not authorized", ErrorType.FORBIDDEN)));
        }

        currentUserService.fromAccessTokenPayload(tokenPayload.get());

        return chain.next(request);
    }

    @NonNull
    @Override
    public WebGraphQlInterceptor andThen(@NonNull WebGraphQlInterceptor nextInterceptor) {
        return WebGraphQlInterceptor.super.andThen(nextInterceptor);
    }

    @NonNull
    @Override
    public Chain apply(@NonNull Chain chain) {
        return WebGraphQlInterceptor.super.apply(chain);
    }

    private WebGraphQlResponse buildExceptionResponse(WebGraphQlResponse response, QueryException ex) {
        List<GraphQLError> errors = response.getErrors().stream()
                .map(error -> GraphqlErrorBuilder.newError()
                        .message(ex.getMessage())
                        .errorType(ex.getErrorType())
                        .build())
                .collect(Collectors.toList());

        return response.transform(builder -> builder.errors(errors).build());
    }
}