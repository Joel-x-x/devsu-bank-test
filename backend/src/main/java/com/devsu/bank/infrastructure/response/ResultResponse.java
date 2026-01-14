package com.devsu.bank.infrastructure.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Standard response wrapper for all API responses.
 * Provides a consistent structure for both success and error responses.
 * 
 * @param <T> The type of the result data
 * @param <E> The type of error information
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultResponse<T, E> {
    private final T result;
    private final boolean isSuccess;
    private final List<E> errors;
    private final String message;
    private final String messageCode;
    private final LocalDateTime timestamp;
    private final int code;

    private ResultResponse(T result, boolean isSuccess, List<E> errors, String message, String messageCode, int code) {
        this.result = result;
        this.isSuccess = isSuccess;
        this.errors = errors;
        this.message = message;
        this.messageCode = messageCode;
        this.timestamp = LocalDateTime.now();
        this.code = code;
    }

    /**
     * Creates a successful response with default message and 200 status.
     * 
     * @param result The result data
     * @return A success response
     */
    public static <T, E> ResultResponse<T, E> success(T result) {
        return success(result, "Operation successful", "OPERATION_SUCCESS", HttpStatus.OK.value());
    }

    /**
     * Creates a successful response with custom message and code.
     * 
     * @param result The result data
     * @param message The success message
     * @param messageCode The message code
     * @param code The HTTP status code
     * @return A success response
     */
    public static <T, E> ResultResponse<T, E> success(T result, String message, String messageCode, int code) {
        return new ResultResponse<>(result, true, null, message, messageCode, code);
    }

    /**
     * Creates a successful response for resource creation with 201 status.
     * 
     * @param result The created resource
     * @return A success response with CREATED status
     */
    public static <T, E> ResultResponse<T, E> created(T result) {
        return success(result, "Resource created successfully", "RESOURCE_CREATED", HttpStatus.CREATED.value());
    }

    /**
     * Creates a successful response for resource creation with custom message.
     * 
     * @param result The created resource
     * @param message The success message
     * @return A success response with CREATED status
     */
    public static <T, E> ResultResponse<T, E> created(T result, String message) {
        return success(result, message, "RESOURCE_CREATED", HttpStatus.CREATED.value());
    }

    /**
     * Creates a successful response for no content with 204 status.
     * 
     * @return A success response with NO_CONTENT status
     */
    public static <T, E> ResultResponse<T, E> noContent() {
        return success(null, "Operation completed successfully", "NO_CONTENT", HttpStatus.NO_CONTENT.value());
    }

    /**
     * Creates a successful response for deletion.
     * 
     * @return A success response with OK status
     */
    public static <T, E> ResultResponse<T, E> deleted() {
        return success(null, "Resource deleted successfully", "RESOURCE_DELETED", HttpStatus.OK.value());
    }

    /**
     * Creates a successful response for update.
     * 
     * @param result The updated resource
     * @return A success response with OK status
     */
    public static <T, E> ResultResponse<T, E> updated(T result) {
        return success(result, "Resource updated successfully", "RESOURCE_UPDATED", HttpStatus.OK.value());
    }

    /**
     * Creates a failure response with errors and status code.
     * 
     * @param errors The list of errors
     * @param code The HTTP status code
     * @return A failure response
     */
    public static <T, E> ResultResponse<T, E> failure(List<E> errors, int code) {
        return failure(null, errors, "OPERATION_FAILED", code);
    }

    /**
     * Creates a failure response with result, errors and custom message code.
     * 
     * @param result The result data (can be null)
     * @param errors The list of errors
     * @param messageCode The message code
     * @param code The HTTP status code
     * @return A failure response
     */
    public static <T, E> ResultResponse<T, E> failure(T result, List<E> errors, String messageCode, int code) {
        String message = messageCode.replace("_", " ").toLowerCase();
        return new ResultResponse<>(result, false, errors, message, messageCode, code);
    }

    /**
     * Creates an error response with a single error message.
     * 
     * @param message The error message
     * @param status The HTTP status
     * @return An error response
     */
    @SuppressWarnings("unchecked")
    public static <T, E> ResultResponse<T, E> error(String message, HttpStatus status) {
        String messageCode = "ERROR_" + status.name();
        return failure(null, Collections.singletonList((E) message), messageCode, status.value());
    }

    /**
     * Creates an error response with custom message code.
     * 
     * @param message The error message
     * @param messageCode The message code
     * @param status The HTTP status
     * @return An error response
     */
    @SuppressWarnings("unchecked")
    public static <T, E> ResultResponse<T, E> error(String message, String messageCode, HttpStatus status) {
        return failure(null, Collections.singletonList((E) message), messageCode, status.value());
    }
}
