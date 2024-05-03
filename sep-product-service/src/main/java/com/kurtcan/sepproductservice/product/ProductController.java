package com.kurtcan.sepproductservice.product;

import com.kurtcan.sepproductservice.product.request.ProductAdd;
import com.kurtcan.sepproductservice.product.request.ProductUpdate;
import com.kurtcan.sepproductservice.shared.result.ErrorResult;
import com.kurtcan.sepproductservice.shared.result.SuccessResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Tag(name = "Products", description = "Product operations")
public class ProductController {

    private final ProductService service;

    @Operation(
            summary = "Get all products with pagination",
            parameters = {
                    @Parameter(
                            name = "search",
                            description = "Search query with operators ':,~,<,>', ex: name~'test',createdAt>'2021-01-01',price:10",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(name = "page", description = "Page number", in = ParameterIn.QUERY),
                    @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY),
                    @Parameter(name = "sort", description = "Sort field name", in = ParameterIn.QUERY),
                    @Parameter(name = "order", description = "Sort order", in = ParameterIn.QUERY),
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
    @GetMapping("")
    public ResponseEntity<?> getProducts(
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order
    ) {
        var pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort));

        Page<Product> products = service.searchProduct(search, pageRequest);
        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Get a product by ID",
            parameters = {
                    @Parameter(name = "productId", description = "ID of the product", required = true, in = ParameterIn.PATH)
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
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable("productId") UUID productId) {
        Product product = service.getProduct(productId);
        return ResponseEntity.ok(product);
    }

    @Operation(
            summary = "Add a new product",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = ProductAdd.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Success",
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
    @PostMapping("")
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductAdd productAdd) {
        Product product = service.addProduct(productAdd);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @Operation(
            summary = "Update a product by ID",
            parameters = {
                    @Parameter(name = "productId", description = "ID of the product", required = true, in = ParameterIn.PATH)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = ProductUpdate.class))),
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
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable("productId") UUID productId, @Valid @RequestBody ProductUpdate productUpdate) {
        Product product = service.updateProduct(productId, productUpdate);
        return ResponseEntity.ok(product);
    }

    @Operation(
            summary = "Delete a product by ID",
            parameters = {
                    @Parameter(name = "productId", description = "ID of the product", required = true, in = ParameterIn.PATH)
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
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable("productId") UUID productId) {
        service.deleteProduct(productId);
        return ResponseEntity.ok(new SuccessResult("Deleted"));
    }

}
