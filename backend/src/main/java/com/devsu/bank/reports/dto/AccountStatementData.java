package com.devsu.bank.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data structure for account statement report.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementData {
    
    // Customer information
    private String customerName;
    private String customerIdentification;
    
    // Report period
    private LocalDateTime reportDate;
    private String startDate;
    private String endDate;
    
    // Accounts with movements
    private List<AccountData> accounts;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountData {
        private String accountNumber;
        private String accountType;
        private BigDecimal initialBalance;
        private BigDecimal finalBalance;
        private BigDecimal dailyLimit;
        private List<MovementData> movements;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovementData {
        private LocalDateTime date;
        private String type;
        private BigDecimal amount;
        private BigDecimal balance;
        private BigDecimal availableBalance;
    }
}
