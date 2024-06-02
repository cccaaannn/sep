package com.kurtcan.sepsearchservice.shared.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ElasticsearchResponse<T> {
    private Hits<T> hits;

    @Data
    public static class Hits<T> {
        private List<Hit<T>> hits;
    }

    public static class Hit<T> {
        private T _source;

        @JsonProperty("_source")
        public T getSource() {
            return _source;
        }

        public void setSource(T source) {
            this._source = source;
        }
    }
}