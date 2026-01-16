package com.devsu.bank.account.dto;

import com.devsu.bank.account.AccountTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String accountNumber,
        AccountTypeEnum accountType,
        BigDecimal initialBalance,
        BigDecimal dailyLimit,
        Boolean status,
        UUID customerId,
        String customerName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
