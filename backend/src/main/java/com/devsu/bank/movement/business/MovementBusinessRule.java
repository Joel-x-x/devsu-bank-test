package com.devsu.bank.movement.business;

import com.devsu.bank.account.business.result.ValidationResult;

/**
 * Interface for movement business rules.
 * Each implementation encapsulates a specific business rule validation for movements.
 */
@FunctionalInterface
public interface MovementBusinessRule {
    /**
     * Validates the business rule.
     * 
     * @return ValidationResult indicating success or failure with details
     */
    ValidationResult validate();
}
