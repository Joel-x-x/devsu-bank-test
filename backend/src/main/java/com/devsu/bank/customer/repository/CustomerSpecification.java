package com.devsu.bank.customer.repository;

import com.devsu.bank.customer.entity.CustomerEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specifications for CustomerEntity filtering.
 * Provides dynamic query building without writing SQL.
 */
public class CustomerSpecification {
    
    /**
     * Creates a specification that filters customers by search value.
     * Searches in name, identification, and customer code fields.
     * Only returns non-deleted customers.
     */
    public static Specification<CustomerEntity> withFilters(String searchValue) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Always filter out deleted records
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));
            
            // Add search filter if provided
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                String searchPattern = "%" + searchValue.toLowerCase() + "%";
                
                Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), 
                    searchPattern
                );
                
                Predicate identificationPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("identification")), 
                    searchPattern
                );
                
                Predicate customerCodePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("customerCode")), 
                    searchPattern
                );
                
                // OR condition: search in any of the fields
                predicates.add(criteriaBuilder.or(
                    namePredicate, 
                    identificationPredicate, 
                    customerCodePredicate
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
