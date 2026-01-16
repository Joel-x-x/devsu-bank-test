package com.devsu.bank.infrastructure.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when trying to perform a transaction on an inactive account.
 * HTTP Status: 422 UNPROCESSABLE ENTITY
 */
public class InactiveAccountException extends BusinessException {
    
    private static final String ERROR_CODE = "ERR_006";
    
    public InactiveAccountException(String message) {
        super(ERROR_CODE, message);
    }
    
    @Override
    public int getHttpStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY.value(); // 422
    }
}
