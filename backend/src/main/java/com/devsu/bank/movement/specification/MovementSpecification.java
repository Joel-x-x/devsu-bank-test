package com.devsu.bank.movement.specification;

import com.devsu.bank.movement.entity.MovementEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for dynamic Movement entity filtering.
 * Uses Criteria API to build type-safe queries without SQL.
 */
public class MovementSpecification {

    /**
     * Creates a specification to filter movements with optional search.
     * Searches across multiple fields: account number, customer name, movement type.
     * 
     * @param searchValue Optional search text (searches account number, customer name, movement type)
     * @return Specification for dynamic query building
     */
    public static Specification<MovementEntity> withFilters(String searchValue) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter out soft-deleted records
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            // Optional: Filter by search value across multiple fields
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                String likePattern = "%" + searchValue.toLowerCase() + "%";

                Predicate accountNumber = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("account").get("accountNumber")),
                        likePattern
                );

                Predicate customerName = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("account").get("customer").get("name")),
                        likePattern
                );

                Predicate movementType = criteriaBuilder.like(
                        criteriaBuilder.lower(criteriaBuilder.treat(root.get("movementType"), String.class)),
                        likePattern
                );

                // Combine search predicates with OR
                predicates.add(criteriaBuilder.or(accountNumber, customerName, movementType));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
