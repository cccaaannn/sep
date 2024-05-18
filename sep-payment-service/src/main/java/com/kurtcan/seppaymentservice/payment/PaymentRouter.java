package com.kurtcan.seppaymentservice.payment;

import com.kurtcan.seppaymentservice.payment.request.PaymentCreate;
import com.kurtcan.seppaymentservice.shared.result.ErrorResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class PaymentRouter {

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/payments",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = PaymentHandler.class,
                    beanMethod = "getAllPayments",
                    operation =
                    @Operation(
                            operationId = "getAllPayments",
                            summary = "Get all payments",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Success",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Payment.class)))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Invalid value",
                                            content = @Content(schema = @Schema(implementation = ErrorResult.class))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResult.class))
                                    ),
                                    @ApiResponse(responseCode = "500", description = "Internal server error",
                                            content = @Content(schema = @Schema(implementation = ErrorResult.class))
                                    )
                            }
                    )
            ),

            @RouterOperation(path = "/payments/{paymentId}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = PaymentHandler.class,
                    beanMethod = "getPaymentById",
                    operation =
                    @Operation(
                            operationId = "getPaymentById",
                            summary = "Get payment by ID",
                            parameters = {
                                    @Parameter(name = "paymentId", description = "ID of the payment", required = true, in = ParameterIn.PATH)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Success",
                                            content = @Content(schema = @Schema(implementation = Payment.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Invalid value",
                                            content = @Content(schema = @Schema(implementation = ErrorResult.class))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResult.class))
                                    ),
                                    @ApiResponse(responseCode = "500", description = "Internal server error",
                                            content = @Content(schema = @Schema(implementation = ErrorResult.class))
                                    )
                            }
                    )
            ),

            @RouterOperation(path = "/payments",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = PaymentHandler.class,
                    beanMethod = "createPayment",
                    operation =
                    @Operation(
                            operationId = "createPayment",
                            summary = "Create a new payment",
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = PaymentCreate.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Success",
                                            content = @Content(schema = @Schema(implementation = Payment.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Invalid value",
                                            content = @Content(schema = @Schema(implementation = ErrorResult.class))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResult.class))
                                    ),
                                    @ApiResponse(responseCode = "500", description = "Internal server error",
                                            content = @Content(schema = @Schema(implementation = ErrorResult.class))
                                    )
                            }
                    )
            ),
    })
    public RouterFunction<ServerResponse> paymentRoute(PaymentHandler paymentHandler) {
        return route()
                .path(apiPrefix, builder -> builder
                        .nest(accept(APPLICATION_JSON), routerBuilder -> routerBuilder
                                .GET("", paymentHandler::getAllPayments)
                                .GET("/{paymentId}", paymentHandler::getPaymentById)
                                .POST("", paymentHandler::createPayment)
                        )
                )
                .build();
    }

}
