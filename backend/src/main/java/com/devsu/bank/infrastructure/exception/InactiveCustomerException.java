package com.devsu.bank.infrastructure.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when trying to perform a transaction for an inactive customer.
 * HTTP Status: 422 UNPROCESSABLE ENTITY
 */
public class InactiveCustomerException extends BusinessException {
    
    private static final String ERROR_CODE = "ERR_007";
    
    public InactiveCustomerException(String message) {
        super(ERROR_CODE, message);
    }
    
    @Override
    public int getHttpStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY.value(); // 422
    }
}
