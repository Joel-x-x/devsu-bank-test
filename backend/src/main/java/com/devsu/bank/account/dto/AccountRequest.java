package com.devsu.bank.account.dto;

import com.devsu.bank.account.AccountTypeEnum;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountRequest(
        @NotNull(message = "Account type is required")
        AccountTypeEnum accountType,
        
        @NotNull(message = "Initial balance is required")
        BigDecimal initialBalance,
        
        @NotNull(message = "Daily limit is required")
        BigDecimal dailyLimit,
        
        @NotNull(message = "Customer ID is required")
        UUID customerId
) {
}
