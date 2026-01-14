package com.devsu.bank.infrastructure.exception;

import com.devsu.bank.infrastructure.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Catches and handles all exceptions, providing consistent error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handles all business exceptions.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResultResponse<ErrorResponse, String>> handleBusinessException(
            BusinessException ex, 
            HttpServletRequest request) {
        
        log.error("Business exception [{}]: {} - Path: {}", 
            ex.getErrorCode(), ex.getMessage(), request.getRequestURI(), ex);
        
        ErrorResponse errorDetail = ErrorResponse.builder()
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .timestamp(ex.getTimestamp())
            .metadata(ex.getMetadata())
            .path(request.getRequestURI())
            .build();
        
        ResultResponse<ErrorResponse, String> response = ResultResponse.failure(
            errorDetail,
            List.of(ex.getMessage()),
            ex.getErrorCode(),
            ex.getHttpStatus()
        );
        
        return ResponseEntity
            .status(ex.getHttpStatus())
            .body(response);
    }
    
    /**
     * Handles validation errors (e.g., @Valid, @NotNull).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultResponse<Map<String, Object>, String>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        log.error("Validation error on path: {}", request.getRequestURI(), ex);
        
        Map<String, Object> validationErrors = new HashMap<>();
        List<String> errorMessages = new ArrayList<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
            errorMessages.add(fieldName + ": " + errorMessage);
        });
        
        ResultResponse<Map<String, Object>, String> response = ResultResponse.failure(
            validationErrors,
            errorMessages,
            "ERR_006",
            HttpStatus.BAD_REQUEST.value()
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }
    
    /**
     * Handles type mismatch errors (e.g., invalid UUID format).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResultResponse<Map<String, Object>, String>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        
        log.error("Type mismatch error on path: {}", request.getRequestURI(), ex);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("parameter", ex.getName());
        metadata.put("value", ex.getValue());
        metadata.put("requiredType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        
        String errorMessage = String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName());
        
        ResultResponse<Map<String, Object>, String> response = ResultResponse.failure(
            metadata,
            List.of(errorMessage),
            "ERR_007",
            HttpStatus.BAD_REQUEST.value()
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }
    
    /**
     * Handles malformed JSON requests.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResultResponse<Void, String>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        
        log.error("Malformed JSON on path: {}", request.getRequestURI(), ex);
        
        ResultResponse<Void, String> response = ResultResponse.error(
            "Malformed JSON request",
            "ERR_008",
            HttpStatus.BAD_REQUEST
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }
    
    /**
     * Handles all other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultResponse<Void, String>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Unexpected exception on path: {}", request.getRequestURI(), ex);
        
        ResultResponse<Void, String> response = ResultResponse.error(
            "An unexpected error occurred",
            "ERR_999",
            HttpStatus.INTERNAL_SERVER_ERROR
        );
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }
}
