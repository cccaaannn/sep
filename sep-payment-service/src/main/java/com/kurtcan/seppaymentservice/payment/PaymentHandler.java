package com.kurtcan.seppaymentservice.payment;

import com.kurtcan.seppaymentservice.payment.request.PaymentCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class PaymentHandler {

    private final PaymentService paymentService;
    private final Validator validator;

    public Mono<ServerResponse> getAllPayments(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentService.getAllPayments(), Payment.class);
    }

    public Mono<ServerResponse> getPaymentById(ServerRequest request) {
        UUID id = UUID.fromString(request.pathVariable("paymentId"));

        return paymentService.getPaymentById(id)
                .flatMap(payment -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(payment))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createPayment(ServerRequest request) {
        Mono<PaymentCreate> paymentMono = request.bodyToMono(PaymentCreate.class);

        return paymentMono
                .doOnNext(this::validate)
                .flatMap(paymentService::createPayment)
                .flatMap(payment -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(payment))
                );
    }

    /**
     * Validate the PaymentCreate object, @Valid annotation is not used because it is not supported in functional endpoints
     *
     * @param paymentCreate PaymentCreate object
     */
    private void validate(PaymentCreate paymentCreate) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(paymentCreate, "paymentCreate");
        validator.validate(paymentCreate, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ServerWebInputException(bindingResult.toString());
        }
    }
}