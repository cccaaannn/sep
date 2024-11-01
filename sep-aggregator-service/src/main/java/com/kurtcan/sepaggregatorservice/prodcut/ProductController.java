package com.kurtcan.sepaggregatorservice.prodcut;

import com.kurtcan.sepaggregatorservice.shared.pagaination.PageImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @QueryMapping
    public PageImpl<ProductWithPayments> products(
            @Argument("search") String search,
            @Argument("page") Integer page,
            @Argument("size") Integer size,
            @Argument("sort") String sort,
            @Argument("order") String order
    ) {
        return productService.getProductWithPayments(search, page, size, sort, order);
    }

}
