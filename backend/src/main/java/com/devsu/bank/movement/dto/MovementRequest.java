package com.devsu.bank.movement.dto;

import com.devsu.bank.movement.entity.MovementTypeEnum;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record MovementRequest(
        @NotNull(message = "Movement type is required")
        MovementTypeEnum movementType,
        
        @NotNull(message = "Amount is required")
        BigDecimal amount,
        
        @NotNull(message = "Account ID is required")
        UUID accountId
) {
}
