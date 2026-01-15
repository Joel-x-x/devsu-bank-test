package com.devsu.bank.account.specification;

import com.devsu.bank.account.entity.AccountEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specifications for AccountEntity filtering.
 * Provides dynamic query building for account searches.
 */
public class AccountSpecification {
    
    /**
     * Creates a specification that filters accounts by search value.
     * Searches in account number and customer name.
     * Only returns non-deleted accounts.
     */
    public static Specification<AccountEntity> withFilters(String searchValue) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Always filter out deleted records
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));
            
            // Add search filter if provided
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                String searchPattern = "%" + searchValue.toLowerCase() + "%";
                
                Predicate accountNumberPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("accountNumber")), 
                    searchPattern
                );
                
                // Search in customer name (join)
                Predicate customerNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("customer").get("name")), 
                    searchPattern
                );
                
                // OR condition: search in account number or customer name
                predicates.add(criteriaBuilder.or(
                    accountNumberPredicate, 
                    customerNamePredicate
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
