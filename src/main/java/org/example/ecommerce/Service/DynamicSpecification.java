package org.example.ecommerce.Service;

import org.springframework.data.jpa.domain.Specification;

public class DynamicSpecification<T> {
    public Specification<T> build(String query) {
        if (query == null || query.isEmpty()) return null;

        Specification<T> spec = (root, query1, cb) -> null;

        String[] filters = query.split(",");
        for (String filter : filters) {
            spec = spec.and(buildSpecificationFromFilter(filter.trim()));
        }

        return spec;
    }

    private Specification<T> buildSpecificationFromFilter(String filter) {
        String[] fieldValue = filter.split(":");

        if (fieldValue.length != 2)
            throw new IllegalArgumentException("Invalid filter format. Expected 'field:operatorValue'");

        String field = fieldValue[0].trim();
        String valueWithOp = fieldValue[1].trim();

        if (valueWithOp.startsWith(">")) {
            String value = valueWithOp.substring(1).trim();
            return (root, query, cb) -> cb.greaterThan(root.get(field), value);
        } else if (valueWithOp.startsWith("<")) {
            String value = valueWithOp.substring(1).trim();
            return (root, query, cb) -> cb.lessThan(root.get(field), value);
        } else if (valueWithOp.startsWith("=")) {
            String value = valueWithOp.substring(1).trim();
            return (root, query, cb) -> cb.equal(root.get(field), value);
        } else {
            return (root, query, cb) -> cb.like(cb.lower(root.get(field)), "%" + valueWithOp.toLowerCase() + "%");
        }
    }
}
