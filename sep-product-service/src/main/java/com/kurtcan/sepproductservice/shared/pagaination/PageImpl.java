package com.kurtcan.sepproductservice.shared.pagaination;

import lombok.Builder;

import java.util.List;

/**
 * PageImpl
 * Spring boot Page implementation causes serialization problems with Redis and Jackson.
 * @param content
 * @param number
 * @param size
 * @param totalElements
 * @param totalPages
 * @param isFirst
 * @param isLast
 * @param numberOfElements
 * @param <T>
 */
@Builder
public record PageImpl<T>(
        List<T> content,
        int number,
        int size,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast,
        int numberOfElements
) {
    public static <T> PageImpl<T> from(org.springframework.data.domain.Page<T> page) {
        return PageImpl.<T>builder()
                .content(page.getContent())
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .numberOfElements(page.getNumberOfElements())
                .build();
    }
}
