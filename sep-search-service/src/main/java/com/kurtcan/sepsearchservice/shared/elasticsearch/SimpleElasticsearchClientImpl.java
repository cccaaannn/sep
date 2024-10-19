package com.kurtcan.sepsearchservice.shared.elasticsearch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kurtcan.sepsearchservice.payment.PaymentElasticIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleElasticsearchClientImpl implements SimpleElasticsearchClient {

    private final ObjectMapper objectMapper;
    private final RestClient client;

    @Override
    public <T extends ElasticsearchEntity> Optional<Response> save(String index, T object) {
        Map<String, Object> objectMap = objectMapper.convertValue(object, new TypeReference<>() {
        });
        Request request = new Request("PUT", MessageFormat.format("/{0}/_doc/{1}", index, object.getId().toString()));
        try {
            request.setJsonEntity(objectMapper.writeValueAsString(objectMap));
            Response response = client.performRequest(request);
            log.debug("{} Save response: {}", index, response.getStatusLine().getStatusCode());
            return Optional.of(response);
        } catch (IOException e) {
            log.error("Error while running elastic query {}: {}", index, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Response> deleteById(String index, UUID id) {
        Request request = new Request("DELETE", MessageFormat.format("/{0}/_doc/{1}", index, id.toString()));
        try {
            Response response = client.performRequest(request);
            log.debug("{} Delete response: {}", index, response.getStatusLine().getStatusCode());
            return Optional.of(response);
        } catch (IOException e) {
            log.error("Error while running elastic query {}: {}", index, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Response> deleteIndex(String index) {
        Request request = new Request("DELETE", MessageFormat.format("/{0}", index));
        try {
            Response response = client.performRequest(request);
            log.debug("{} Delete response: {}", index, response.getStatusLine().getStatusCode());
            return Optional.of(response);
        } catch (IOException e) {
            log.error("Error while running elastic query {}: {}", index, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public <T> Optional<List<T>> searchMultiFuzzy(String index, Map<String, String> searchMap, Class<T> clas) {
        Request request = new Request("GET", MessageFormat.format("/{0}/_search", index));

        StringBuilder body = new StringBuilder();

        body.append("{\"query\": {\"multi_match\": {");
        body.append("\"query\": ")
                .append("\"").append(String.join(" ", searchMap.values())).append("\"").append(",")
                .append("\"fields\": [")
                .append(searchMap.keySet().stream().map(key -> MessageFormat.format("\"{0}\"", key))
                        .collect(Collectors.joining(",")))
                .append("],\"fuzziness\": 2");
        body.append("}}}");

        log.debug("Search body: {}", body);

        try {
            request.setJsonEntity(body.toString());
            Response response = client.performRequest(request);
            ElasticsearchResponse<T> esResponse = objectMapper.readValue(
                    EntityUtils.toString(response.getEntity()),
                    objectMapper.getTypeFactory().constructParametricType(ElasticsearchResponse.class, clas));

            List<T> results = esResponse.getHits().getHits().stream()
                    .map(ElasticsearchResponse.Hit::getSource)
                    .collect(Collectors.toList());
            return Optional.of(results);
        } catch (IOException e) {
            log.error("Error while running elastic query {}: {}", index, e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public <T> Optional<List<T>> searchFuzzy(String index, String key, String value, Class<T> clas) {
        Request request = new Request("GET", MessageFormat.format("/{0}/_search", index));

        StringBuilder body = new StringBuilder();

        body.append("{\"query\": {\"fuzzy\": {");
        body.append("\"").append(key).append("\"").append(": ")
                .append("{")
                .append("\"value\": \"").append(value).append("\",")
                .append("\"fuzziness\": 2")
                .append("}");
        body.append("}}}");

        log.debug("Search body: {}", body);

        try {
            request.setJsonEntity(body.toString());
            Response response = client.performRequest(request);
            ElasticsearchResponse<T> esResponse = objectMapper.readValue(
                    EntityUtils.toString(response.getEntity()),
                    objectMapper.getTypeFactory().constructParametricType(ElasticsearchResponse.class, clas));

            List<T> results = esResponse.getHits().getHits().stream()
                    .map(ElasticsearchResponse.Hit::getSource)
                    .collect(Collectors.toList());
            return Optional.of(results);
        } catch (IOException e) {
            log.error("Error while running elastic query {}: {}", index, e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public <T> Optional<List<T>> match(String index, String key, String value, Class<T> clas) {
        Request request = new Request("GET", MessageFormat.format("/{0}/_search", index));

        StringBuilder body = new StringBuilder();
        body.append("{\"query\": {\"match\": {");
        // body.append("{\"query\": {\"match_phrase_prefix\": {");
        body.append("\"").append(key).append("\": \"").append(value).append("\"");
        body.append("}}}");

        log.debug("Search body: {}", body);

        try {
            request.setJsonEntity(body.toString());
            Response response = client.performRequest(request);
            ElasticsearchResponse<T> esResponse = objectMapper.readValue(
                    EntityUtils.toString(response.getEntity()),
                    objectMapper.getTypeFactory().constructParametricType(ElasticsearchResponse.class, clas));

            List<T> results = esResponse.getHits().getHits().stream()
                    .map(ElasticsearchResponse.Hit::getSource)
                    .collect(Collectors.toList());
            return Optional.of(results);
        } catch (IOException e) {
            log.error("Error while running elastic query {}: {}", index, e.getMessage());
        }

        return Optional.empty();
    }

}
