package com.devsu.bank.reports.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for generating account statements.
 */
public record AccountStatementRequest(
        @NotNull(message = "Customer ID is required")
        UUID customerId,
        
        @NotNull(message = "Start date is required")
        LocalDate startDate,
        
        @NotNull(message = "End date is required")
        LocalDate endDate
) {
}
