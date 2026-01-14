package com.devsu.bank.infrastructure.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an entity is not found in the database.
 * HTTP Status: 404 NOT FOUND
 */
public class EntityNotFoundException extends BusinessException {
    
    private static final String ERROR_CODE = "ERR_001";
    
    public EntityNotFoundException(String message) {
        super(ERROR_CODE, message);
    }
    
    public EntityNotFoundException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
    
    public EntityNotFoundException(String entityName, Object id) {
        super(ERROR_CODE, String.format("%s not found with id: %s", entityName, id));
        withMetadata("entityName", entityName);
        withMetadata("entityId", id);
    }
    
    @Override
    public int getHttpStatus() {
        return HttpStatus.NOT_FOUND.value(); // 404
    }
}
