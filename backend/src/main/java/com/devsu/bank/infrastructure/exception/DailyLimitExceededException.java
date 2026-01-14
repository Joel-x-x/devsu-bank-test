package com.devsu.bank.infrastructure.exception;

import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

/**
 * Exception thrown when a daily transaction limit is exceeded.
 * HTTP Status: 422 UNPROCESSABLE ENTITY
 */
public class DailyLimitExceededException extends BusinessException {
    
    private static final String ERROR_CODE = "ERR_005";
    
    public DailyLimitExceededException(String message) {
        super(ERROR_CODE, message);
    }
    
    public DailyLimitExceededException(BigDecimal dailyLimit, BigDecimal currentUsage, BigDecimal requestedAmount) {
        super(ERROR_CODE, String.format("Daily limit exceeded. Limit: %s, Current usage: %s, Requested: %s", 
            dailyLimit, currentUsage, requestedAmount));
        withMetadata("dailyLimit", dailyLimit);
        withMetadata("currentUsage", currentUsage);
        withMetadata("requestedAmount", requestedAmount);
        withMetadata("remaining", dailyLimit.subtract(currentUsage));
    }
    
    @Override
    public int getHttpStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY.value(); // 422
    }
}
