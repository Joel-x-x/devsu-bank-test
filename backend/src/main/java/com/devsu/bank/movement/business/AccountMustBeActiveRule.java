package com.devsu.bank.movement.business;

import com.devsu.bank.account.business.result.ValidationResult;
import com.devsu.bank.account.entity.AccountEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Business rule: Account must be active to perform transactions.
 * Ensures movements are only created for operational accounts.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountMustBeActiveRule implements MovementBusinessRule {
    
    private AccountEntity account;
    
    /**
     * Sets the account to validate.
     */
    public AccountMustBeActiveRule forAccount(AccountEntity account) {
        this.account = account;
        return this;
    }
    
    @Override
    public ValidationResult validate() {
        log.debug("Validating account active rule for account: {}", 
                account != null ? account.getAccountNumber() : "null");
        
        if (account == null) {
            return ValidationResult.failure("Account is required", "ERR_ACCOUNT_NULL");
        }
        
        if (!account.getStatus()) {
            log.warn("Account {} is inactive", account.getAccountNumber());
            return ValidationResult.failure(
                    "Cannot perform transaction on inactive account: " + account.getAccountNumber(),
                    "ERR_ACCOUNT_INACTIVE"
            );
        }
        
        log.debug("Account {} is active", account.getAccountNumber());
        return ValidationResult.success();
    }
}
