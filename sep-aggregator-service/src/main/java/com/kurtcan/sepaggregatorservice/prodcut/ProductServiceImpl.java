package com.kurtcan.sepaggregatorservice.prodcut;

import com.kurtcan.sepaggregatorservice.payment.Payment;
import com.kurtcan.sepaggregatorservice.payment.PaymentService;
import com.kurtcan.sepaggregatorservice.shared.cache.CacheName;
import com.kurtcan.sepaggregatorservice.shared.circuitbreaker.CircuitBreakerName;
import com.kurtcan.sepaggregatorservice.shared.exception.QueryException;
import com.kurtcan.sepaggregatorservice.shared.jwt.TokenClient;
import com.kurtcan.sepaggregatorservice.shared.pagaination.PageImpl;
import com.kurtcan.sepaggregatorservice.shared.pagaination.PageMapper;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Builder
@Component
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final PageMapper pageMapper;
    private final TokenClient tokenClient;
    private final CurrentUserService currentUserService;

    @Qualifier(CircuitBreakerName.PRODUCT)
    private final CircuitBreaker productServiceCircuitBreaker;
    private final ProductServiceClient productServiceClient;

    private final PaymentService paymentService;

    @Override
    @Cacheable(value = CacheName.PRODUCTS, keyGenerator = "userAwareCacheKeyGenerator")
    public PageImpl<ProductWithPayments> getProductWithPayments(String search, Integer page, Integer size, String sort, String order) {
        var currentUser = currentUserService.getCurrentUser();
        if (currentUser.isEmpty()) {
            throw new QueryException("Current user not found");
        }
        UUID userId = currentUser.get().getId();

        String bearerToken = acquiredBearerToken();

        // Start fetching products and payments concurrently
        CompletableFuture<Optional<PageImpl<Product>>> productsFuture = CompletableFuture.supplyAsync(() ->
                productServiceCircuitBreaker.run(
                        () -> productServiceClient.getAllProducts(search, page, size, sort, order, bearerToken),
                        throwable -> {
                            log.error("Error while fetching products from service: {}", throwable.getMessage());
                            return Optional.empty();
                        })
        );

        CompletableFuture<List<Payment>> paymentsFuture = CompletableFuture.supplyAsync(() -> paymentService.getUserPayments(userId));

        // If products are not found, return empty page without waiting for payments
        Optional<PageImpl<Product>> productsOptional = productsFuture.join();
        if (productsOptional.isEmpty()) {
            return PageImpl.empty();
        }

        List<Payment> paymentList = paymentsFuture.join();

        // Map products to ProductWithPayments
        PageImpl<ProductWithPayments> productWithPaymentsPage = pageMapper.mapPaginatedList(productsOptional.get(), ProductWithPayments.class);

        if (paymentList.isEmpty()) {
            return productWithPaymentsPage;
        }

        var productPayments = groupPaymentsByProductId(paymentList);

        for (var productWithPayment : productWithPaymentsPage.content()) {
            productWithPayment.setPayments(productPayments.get(productWithPayment.getId()));
        }

        return productWithPaymentsPage;
    }

    private String acquiredBearerToken() {
        Optional<TokenClient.TokenResponse> tokenOptional = tokenClient.getTokenWithCircuitBreaker();
        if (tokenOptional.isEmpty()) {
            log.error("Cannot acquire token");
            throw new QueryException("Cannot acquire token", ErrorType.INTERNAL_ERROR);
        }
        log.info("Acquired access token: {}", tokenOptional.get().accessToken());

        return MessageFormat.format("Bearer {0}", tokenOptional.get().accessToken());
    }

    private Map<UUID, List<Payment>> groupPaymentsByProductId(List<Payment> payments) {
        Map<UUID, List<Payment>> productPayments = new HashMap<>();
        for (var payment : payments) {
            if (!productPayments.containsKey(payment.getProductId())) {
                productPayments.put(payment.getProductId(), new ArrayList<>());
            }
            productPayments.get(payment.getProductId()).add(payment);
        }
        return productPayments;
    }

}
