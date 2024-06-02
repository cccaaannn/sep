package com.kurtcan.sepsearchservice.product;

import java.util.List;

public interface ProductService {
    List<Product> match(String name);

    List<Product> searchMultiFuzzy(String name, String description);

    List<Product> searchFuzzy(String name);
}
