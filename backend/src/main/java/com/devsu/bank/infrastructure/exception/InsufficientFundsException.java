package com.devsu.bank.infrastructure.exception;

import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

/**
 * Exception thrown when an account has insufficient funds for a transaction.
 * HTTP Status: 422 UNPROCESSABLE ENTITY
 */
public class InsufficientFundsException extends BusinessException {
    
    private static final String ERROR_CODE = "ERR_004";
    
    public InsufficientFundsException(String message) {
        super(ERROR_CODE, message);
    }
    
    public InsufficientFundsException(BigDecimal availableBalance, BigDecimal requestedAmount) {
        super(ERROR_CODE, String.format("Insufficient funds. Available: %s, Requested: %s", 
            availableBalance, requestedAmount));
        withMetadata("availableBalance", availableBalance);
        withMetadata("requestedAmount", requestedAmount);
    }
    
    @Override
    public int getHttpStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY.value(); // 422
    }
}
