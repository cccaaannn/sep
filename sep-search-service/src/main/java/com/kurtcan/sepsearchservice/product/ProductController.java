package com.kurtcan.sepsearchservice.product;

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
@RequestMapping("${api.prefix}/products")
@Tag(name = "Products", description = "Product search operations")
public class ProductController extends BaseController {

    private final ProductService service;

    @Operation(
            summary = "Fuzzy search products with name or description",
            parameters = {
                    @Parameter(
                            name = "name",
                            description = "Product name to search",
                            in = ParameterIn.QUERY,
                            required = false
                    ),
                    @Parameter(
                            name = "description",
                            description = "Product description to search",
                            in = ParameterIn.QUERY,
                            required = false
                    ),
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
    @GetMapping("/fuzzy")
    public ResponseEntity<?> fuzzy(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "description", required = false) String description
    ) {
        return ok(service.searchMultiFuzzy(name, description));
    }

    @Operation(
            summary = "Get all products matching with a name",
            parameters = {
                    @Parameter(
                            name = "name",
                            description = "Product name to match, has to be exact name",
                            in = ParameterIn.QUERY,
                            required = true
                    ),
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
            @RequestParam(name = "name") String name
    ) {
        return ok(service.match(name));
    }

}
