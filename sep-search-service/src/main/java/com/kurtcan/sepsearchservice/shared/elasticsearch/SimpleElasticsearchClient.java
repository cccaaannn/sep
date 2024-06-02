package com.kurtcan.sepsearchservice.shared.elasticsearch;

import org.elasticsearch.client.Response;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface SimpleElasticsearchClient {
    <T extends ElasticsearchEntity> Optional<Response> save(String index, T object);

    Optional<Response> deleteById(String index, UUID id);

    Optional<Response> deleteIndex(String index);

    <T> Optional<List<T>> searchMultiFuzzy(String index, Map<String, String> searchMap, Class<T> clas);

    <T> Optional<List<T>> searchFuzzy(String index, String key, String value, Class<T> clas);

    <T> Optional<List<T>> match(String index, String key, String value, Class<T> clas);
}
