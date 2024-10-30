package com.kurtcan.sepaggregatorservice.prodcut;

import com.kurtcan.sepaggregatorservice.shared.pagaination.PageImpl;

public interface ProductService {
    PageImpl<ProductWithPayments> getProductWithPayments(String search, Integer page, Integer size, String sort, String order);
}
