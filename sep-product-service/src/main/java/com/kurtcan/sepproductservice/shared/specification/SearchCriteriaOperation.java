package com.kurtcan.sepproductservice.shared.specification;

import lombok.Getter;

@Getter
public enum SearchCriteriaOperation {
    EQUAL(":"),
    LIKE("~"),
    LESS("<"),
    GREATER(">");

    private final String value;

    private SearchCriteriaOperation(String value) {
        this.value = value;
    }

}
