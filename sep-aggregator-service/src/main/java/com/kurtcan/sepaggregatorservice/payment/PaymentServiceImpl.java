package com.kurtcan.sepaggregatorservice.payment;

import com.kurtcan.sepaggregatorservice.shared.cache.CacheName;
import com.kurtcan.sepaggregatorservice.shared.circuitbreaker.CircuitBreakerName;
import com.kurtcan.sepaggregatorservice.shared.exception.QueryException;
import com.kurtcan.sepaggregatorservice.shared.jwt.TokenClient;
import com.kurtcan.sepaggregatorservice.shared.security.CurrentUserService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Builder
@Component
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final TokenClient tokenClient;
    private final CurrentUserService currentUserService;

    @Qualifier(CircuitBreakerName.PAYMENT)
    private final CircuitBreaker paymentServiceCircuitBreaker;
    private final PaymentServiceClient paymentServiceClient;

    @Override
    @Cacheable(value = CacheName.PAYMENTS, keyGenerator = "userAwareCacheKeyGenerator")
    public List<Payment> getUserPayments() {
        var currentUser = currentUserService.getCurrentUser();
        if (currentUser.isEmpty()) {
            throw new QueryException("Current user not found");
        }
        UUID userId = currentUser.get().getId();

        String bearerToken = acquiredBearerToken();

        Optional<List<Payment>> paymentOptional = paymentServiceCircuitBreaker.run(
                () -> paymentServiceClient.getByUserId(userId, bearerToken),
                throwable -> {
                    log.error("Error while fetching product from service: {}", throwable.getMessage());
                    return Optional.empty();
                });

        return paymentOptional.orElseGet(List::of);
    }

    @Override
    @Cacheable(value = CacheName.PAYMENTS, key = "#userId")
    public List<Payment> getUserPayments(UUID userId) {
        String bearerToken = acquiredBearerToken();

        Optional<List<Payment>> paymentOptional = paymentServiceCircuitBreaker.run(
                () -> paymentServiceClient.getByUserId(userId, bearerToken),
                throwable -> {
                    log.error("Error while fetching product from service: {}", throwable.getMessage());
                    return Optional.empty();
                });

        return paymentOptional.orElseGet(List::of);
    }

    private String acquiredBearerToken() {
        Optional<TokenClient.TokenResponse> tokenOptional = tokenClient.getTokenWithCircuitBreaker();
        if (tokenOptional.isEmpty()) {
            PaymentServiceImpl.log.error("Cannot acquire token");
            throw new QueryException("Cannot acquire token", ErrorType.INTERNAL_ERROR);
        }
        PaymentServiceImpl.log.info("Acquired access token: {}", tokenOptional.get().accessToken());

        return MessageFormat.format("Bearer {0}", tokenOptional.get().accessToken());
    }

}
