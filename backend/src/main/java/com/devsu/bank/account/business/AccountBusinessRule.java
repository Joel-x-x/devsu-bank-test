package com.devsu.bank.account.business;

import com.devsu.bank.account.business.result.ValidationResult;

/**
 * Interface for account business rules.
 * Each implementation encapsulates a specific business rule validation.
 */
@FunctionalInterface
public interface AccountBusinessRule {
    /**
     * Validates the business rule.
     * 
     * @return ValidationResult indicating success or failure with details
     */
    ValidationResult validate();
}
