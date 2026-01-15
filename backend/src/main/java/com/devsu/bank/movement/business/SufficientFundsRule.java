package com.devsu.bank.movement.business;

import com.devsu.bank.account.business.result.ValidationResult;
import com.devsu.bank.account.entity.AccountEntity;
import com.devsu.bank.movement.entity.MovementTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Business rule: Account must have sufficient funds for debit transactions.
 * Validates that current balance can cover the debit amount.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SufficientFundsRule implements MovementBusinessRule {
    
    private AccountEntity account;
    private BigDecimal amount;
    private BigDecimal currentBalance;
    private MovementTypeEnum movementType;
    
    /**
     * Sets the transaction details to validate.
     */
    public SufficientFundsRule forTransaction(
            AccountEntity account, 
            BigDecimal amount, 
            BigDecimal currentBalance,
            MovementTypeEnum movementType) {
        this.account = account;
        this.amount = amount;
        this.currentBalance = currentBalance;
        this.movementType = movementType;
        return this;
    }
    
    @Override
    public ValidationResult validate() {
        log.debug("Validating sufficient funds for account: {}, amount: {}, current balance: {}", 
                account != null ? account.getAccountNumber() : "null", amount, currentBalance);
        
        // Only validate for debit transactions
        if (movementType != MovementTypeEnum.DEBIT) {
            log.debug("Skipping validation for non-debit transaction");
            return ValidationResult.success();
        }
        
        if (account == null || amount == null || currentBalance == null) {
            return ValidationResult.failure(
                    "Account, amount, and current balance are required",
                    "ERR_MISSING_PARAMS"
            );
        }
        
        BigDecimal balanceAfterDebit = currentBalance.subtract(amount);
        
        if (balanceAfterDebit.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Insufficient funds for account {}. Current: {}, Requested: {}", 
                    account.getAccountNumber(), currentBalance, amount);
            return ValidationResult.failure(
                    String.format("Insufficient funds. Current balance: %s, Requested: %s", 
                            currentBalance, amount),
                    "ERR_INSUFFICIENT_FUNDS"
            );
        }
        
        log.debug("Sufficient funds available. Balance after debit: {}", balanceAfterDebit);
        return ValidationResult.success();
    }
}
