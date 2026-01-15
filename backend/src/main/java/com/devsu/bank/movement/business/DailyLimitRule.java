package com.devsu.bank.movement.business;

import com.devsu.bank.account.business.result.ValidationResult;
import com.devsu.bank.account.entity.AccountEntity;
import com.devsu.bank.movement.entity.MovementTypeEnum;
import com.devsu.bank.movement.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Business rule: Daily debit limit must not be exceeded.
 * Validates that the sum of today's debits + new debit does not exceed the account's daily limit.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyLimitRule implements MovementBusinessRule {
    
    private final MovementRepository movementRepository;
    
    private AccountEntity account;
    private BigDecimal amount;
    private MovementTypeEnum movementType;
    
    /**
     * Sets the transaction details to validate.
     */
    public DailyLimitRule forTransaction(
            AccountEntity account, 
            BigDecimal amount,
            MovementTypeEnum movementType) {
        this.account = account;
        this.amount = amount;
        this.movementType = movementType;
        return this;
    }
    
    @Override
    public ValidationResult validate() {
        log.debug("Validating daily limit for account: {}, amount: {}", 
                account != null ? account.getAccountNumber() : "null", amount);
        
        // Only validate for debit transactions
        if (movementType != MovementTypeEnum.DEBIT) {
            log.debug("Skipping validation for non-debit transaction");
            return ValidationResult.success();
        }
        
        if (account == null || amount == null) {
            return ValidationResult.failure(
                    "Account and amount are required",
                    "ERR_MISSING_PARAMS"
            );
        }
        
        // Calculate today's range
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        
        // Get sum of today's debits
        BigDecimal todayDebits = movementRepository
                .sumDebitsByAccountAndDateRange(account.getId(), startOfDay, endOfDay);
        
        if (todayDebits == null) {
            todayDebits = BigDecimal.ZERO;
        }
        
        BigDecimal totalAfterTransaction = todayDebits.add(amount);
        
        if (totalAfterTransaction.compareTo(account.getDailyLimit()) > 0) {
            log.warn("Daily limit exceeded for account {}. Limit: {}, Today's debits: {}, Requested: {}", 
                    account.getAccountNumber(), account.getDailyLimit(), todayDebits, amount);
            return ValidationResult.failure(
                    String.format("Daily limit exceeded. Limit: %s, Used today: %s, Requested: %s, Remaining: %s", 
                            account.getDailyLimit(), 
                            todayDebits, 
                            amount,
                            account.getDailyLimit().subtract(todayDebits)),
                    "ERR_DAILY_LIMIT_EXCEEDED"
            );
        }
        
        log.debug("Daily limit check passed. Limit: {}, Used: {}, After transaction: {}", 
                account.getDailyLimit(), todayDebits, totalAfterTransaction);
        return ValidationResult.success();
    }
}
