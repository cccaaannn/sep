package com.kurtcan.seppaymentservice.shared.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kurtcan.seppaymentservice.shared.result.ErrorResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Order(-10)
@Configuration
@RequiredArgsConstructor
public class ExceptionHandlerConfig implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        ErrorResult errorResult = ErrorResult.builder().build();

        switch (ex) {
            case IllegalArgumentException illegalArgumentException -> {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                errorResult.setMessage("Validation error");
            }
            case ResourceNotFoundException resourceNotFoundException -> {
                exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                errorResult.setMessage("Resource not found");
            }
            case TypeMismatchException typeMismatchException -> {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                errorResult.setMessage("Type mismatch error");
            }
            case HttpMessageNotReadableException httpMessageNotReadableException -> {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                errorResult.setMessage("Message not readable");
            }
            default -> {
                exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                errorResult.setMessage(Objects.nonNull(ex.getMessage()) ? ex.getMessage() : "Internal server error");
            }
        }

        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResult);
            var buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (
                JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}