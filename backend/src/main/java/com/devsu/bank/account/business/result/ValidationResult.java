package com.devsu.bank.account.business.result;

/**
 * Result of a business rule validation.
 * Immutable record containing validation status, message, and error code.
 */
public record ValidationResult(
        boolean isValid,
        String message,
        String errorCode
) {
    /**
     * Creates a successful validation result.
     */
    public static ValidationResult success() {
        return new ValidationResult(true, null, null);
    }
    
    /**
     * Creates a failed validation result with message and error code.
     */
    public static ValidationResult failure(String message, String errorCode) {
        return new ValidationResult(false, message, errorCode);
    }
    
    /**
     * Creates a failed validation result with only message.
     */
    public static ValidationResult failure(String message) {
        return new ValidationResult(false, message, "VALIDATION_ERROR");
    }
}
