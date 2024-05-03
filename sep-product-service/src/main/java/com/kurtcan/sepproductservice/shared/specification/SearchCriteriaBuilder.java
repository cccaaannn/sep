package com.kurtcan.sepproductservice.shared.specification;

import java.util.Optional;

public class SearchCriteriaBuilder {
    public static Optional<SearchCriteria> build(String criteriaStr) {
        SearchCriteriaOperation operation;
        String[] keyValue;

        if (criteriaStr.contains(SearchCriteriaOperation.EQUAL.getValue())) {
            operation = SearchCriteriaOperation.EQUAL;
            keyValue = criteriaStr.split(SearchCriteriaOperation.EQUAL.getValue(), 2);
        } else if (criteriaStr.contains(SearchCriteriaOperation.LESS.getValue())) {
            operation = SearchCriteriaOperation.LESS;
            keyValue = criteriaStr.split(SearchCriteriaOperation.LESS.getValue(), 2);
        } else if (criteriaStr.contains(SearchCriteriaOperation.GREATER.getValue())) {
            operation = SearchCriteriaOperation.GREATER;
            keyValue = criteriaStr.split(SearchCriteriaOperation.GREATER.getValue(), 2);
        } else if (criteriaStr.contains(SearchCriteriaOperation.LIKE.getValue())) {
            operation = SearchCriteriaOperation.LIKE;
            keyValue = criteriaStr.split(SearchCriteriaOperation.LIKE.getValue(), 2);
        } else {
            return Optional.empty();
        }

        if (keyValue.length != 2) return Optional.empty();

        String key = keyValue[0];
        String value = keyValue[1];

        return Optional.of(
                SearchCriteria.builder()
                        .key(key)
                        .operation(operation)
                        .value(value)
                        .build()
        );
    }
}
