package com.kurtcan.sepaggregatorservice.shared.pagaination;

import lombok.Builder;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PageImpl
 * Spring boot Page requires spring data jpa dependency.
 *
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
    public static <T> PageImpl<T> empty() {
        return PageImpl.<T>builder()
                .content(List.of())
                .number(0)
                .size(0)
                .totalElements(0)
                .totalPages(0)
                .isFirst(true)
                .isLast(true)
                .numberOfElements(0)
                .build();
    }

    public <U> PageImpl<U> map(Function<? super T, ? extends U> converter) {
        List<U> convertedContent = content.stream()
                .map(converter)
                .collect(Collectors.toList());

        return PageImpl.<U>builder()
                .content(convertedContent)
                .number(number)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .isFirst(isFirst)
                .isLast(isLast)
                .numberOfElements(numberOfElements)
                .build();
    }
}
