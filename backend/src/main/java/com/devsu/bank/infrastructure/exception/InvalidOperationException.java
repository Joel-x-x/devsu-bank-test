package com.devsu.bank.infrastructure.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an operation is not valid in the current context.
 * HTTP Status: 400 BAD REQUEST
 */
public class InvalidOperationException extends BusinessException {
    
    private static final String ERROR_CODE = "ERR_003";
    
    public InvalidOperationException(String message) {
        super(ERROR_CODE, message);
    }
    
    public InvalidOperationException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
    
    @Override
    public int getHttpStatus() {
        return HttpStatus.BAD_REQUEST.value(); // 400
    }
}
