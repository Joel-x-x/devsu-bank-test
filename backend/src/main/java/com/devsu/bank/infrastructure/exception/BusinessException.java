package com.devsu.bank.infrastructure.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all business exceptions in the application.
 * Provides common functionality like error codes, timestamps, and metadata.
 */
@Getter
public abstract class BusinessException extends RuntimeException {
    
    private final String errorCode;
    private final LocalDateTime timestamp;
    private final Map<String, Object> metadata;
    
    protected BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
        this.metadata = new HashMap<>();
    }
    
    protected BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
        this.metadata = new HashMap<>();
    }
    
    /**
     * Adds metadata to the exception for additional context.
     * Returns this for method chaining.
     * 
     * @param key The metadata key
     * @param value The metadata value
     * @return This exception instance for chaining
     */
    public BusinessException withMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }
    
    /**
     * Returns the HTTP status code associated with this exception.
     * Subclasses must override to provide specific status codes.
     * 
     * @return The HTTP status code
     */
    public abstract int getHttpStatus();
}
