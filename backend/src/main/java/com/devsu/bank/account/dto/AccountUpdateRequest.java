package com.devsu.bank.account.dto;

import com.devsu.bank.account.entity.AccountTypeEnum;

import java.math.BigDecimal;

public record AccountUpdateRequest(
        AccountTypeEnum accountType,
        BigDecimal dailyLimit
) {
}
