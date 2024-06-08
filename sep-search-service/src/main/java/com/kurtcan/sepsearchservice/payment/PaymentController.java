package com.kurtcan.sepsearchservice.payment;

import com.kurtcan.sepsearchservice.product.Product;
import com.kurtcan.sepsearchservice.shared.controller.BaseController;
import com.kurtcan.sepsearchservice.shared.result.ErrorResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/payments")
@Tag(name = "Payments", description = "Payment search operations")
public class PaymentController extends BaseController {

    private final PaymentService service;

    @Operation(
            summary = "Get all payments matching with a amount",
            parameters = {
                    @Parameter(
                            name = "amount",
                            description = "Payment amount to match, has to be exact amount",
                            in = ParameterIn.QUERY,
                            required = false
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(schema = @Schema(implementation = Product.class))
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
    @GetMapping("/match")
    public ResponseEntity<?> match(
            @RequestParam(name = "amount", required = false) int amount
    ) {
        return ok(service.match(amount));
    }

}
