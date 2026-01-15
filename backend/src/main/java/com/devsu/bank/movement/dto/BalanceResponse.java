package com.devsu.bank.movement.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO for balance calculation queries.
 * Provides current balance and daily limit information.
 */
public record BalanceResponse(
        UUID accountId,
        String accountNumber,
        BigDecimal initialBalance,
        BigDecimal currentBalance,
        BigDecimal dailyLimit,
        BigDecimal dailyLimitUsed,
        BigDecimal dailyLimitRemaining
) {
}
