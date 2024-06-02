package com.kurtcan.sepsearchservice.product;

import com.kurtcan.sepsearchservice.shared.elasticsearch.SimpleElasticsearchClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final SimpleElasticsearchClient elasticSearchClient;

    @Override
    public List<Product> match(String name) {
        return elasticSearchClient.match("products", "name", name, Product.class).orElse(List.of());
    }

    @Override
    public List<Product> searchMultiFuzzy(String name, String description) {

        Map<String, String> searchMap = new HashMap<>();
        if (Objects.nonNull(name)) {
            searchMap.put("name", name);
        }
        if (Objects.nonNull(description)) {
            searchMap.put("description", description);
        }

        return elasticSearchClient.searchMultiFuzzy("products", searchMap, Product.class).orElse(List.of());
    }

    @Override
    public List<Product> searchFuzzy(String name) {
        return elasticSearchClient.searchFuzzy("products", "name", name, Product.class).orElse(List.of());
    }
}
