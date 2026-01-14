package com.devsu.bank.movement.dto;

import com.devsu.bank.movement.entity.MovementTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MovementResponse(
        UUID id,
        LocalDateTime movementDate,
        MovementTypeEnum movementType,
        BigDecimal amount,
        BigDecimal balance,
        BigDecimal availableBalance,
        UUID accountId,
        String accountNumber,
        LocalDateTime createdAt
) {
}
