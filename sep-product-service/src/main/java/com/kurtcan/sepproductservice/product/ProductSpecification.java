package com.kurtcan.sepproductservice.product;

import com.kurtcan.sepproductservice.shared.specification.SearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

@AllArgsConstructor
public class ProductSpecification implements Specification<Product> {

    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        if (root.get(criteria.getKey()).getJavaType().equals(OffsetDateTime.class)) {
            try {
                String criteriaValue = criteria.getValue().toString();

                OffsetDateTime offsetDateTime = criteria.toString().contains("T")
                        ? OffsetDateTime.parse(criteriaValue)
                        : OffsetDateTime.parse(MessageFormat.format("{0}T00:00:00+00:00", criteriaValue));

                if (criteria.getOperation().getValue().equalsIgnoreCase(">")) {
                    return builder.greaterThanOrEqualTo(root.get(criteria.getKey()), offsetDateTime);
                } else if (criteria.getOperation().getValue().equalsIgnoreCase("<")) {
                    return builder.lessThanOrEqualTo(root.get(criteria.getKey()), offsetDateTime);
                } else if (criteria.getOperation().getValue().equalsIgnoreCase(":")) {
                    return builder.equal(root.get(criteria.getKey()), offsetDateTime);
                }
            } catch (DateTimeParseException ex) {
                return null;
            }
        } else if (criteria.getOperation().getValue().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
        } else if (criteria.getOperation().getValue().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
        } else if (criteria.getOperation().getValue().equalsIgnoreCase(":")) {
            return builder.equal(root.get(criteria.getKey()), criteria.getValue().toString());
        } else if (criteria.getOperation().getValue().equalsIgnoreCase("~")) {
            if (root.get(criteria.getKey()).getJavaType().equals(String.class)) {
                return builder.like(root.get(criteria.getKey()), MessageFormat.format("%{0}%", criteria.getValue()));
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}