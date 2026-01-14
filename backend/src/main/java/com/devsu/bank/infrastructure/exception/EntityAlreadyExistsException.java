package com.devsu.bank.infrastructure.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there's a conflict with existing data.
 * HTTP Status: 409 CONFLICT
 */
public class EntityAlreadyExistsException extends BusinessException {
    
    private static final String ERROR_CODE = "ERR_002";
    
    public EntityAlreadyExistsException(String message) {
        super(ERROR_CODE, message);
    }
    
    public EntityAlreadyExistsException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
    
    public EntityAlreadyExistsException(String entityName, String field, Object value) {
        super(ERROR_CODE, String.format("%s already exists with %s: %s", entityName, field, value));
        withMetadata("entityName", entityName);
        withMetadata("field", field);
        withMetadata("value", value);
    }
    
    @Override
    public int getHttpStatus() {
        return HttpStatus.CONFLICT.value(); // 409
    }
}
