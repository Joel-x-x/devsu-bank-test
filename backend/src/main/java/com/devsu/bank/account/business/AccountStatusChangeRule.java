package com.devsu.bank.account.business;

import com.devsu.bank.account.business.result.ValidationResult;
import com.devsu.bank.account.entity.AccountEntity;
import com.devsu.bank.customer.entity.CustomerEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Business rule: Account status changes must comply with business constraints.
 * - Cannot activate account if customer is inactive
 * - Cannot deactivate account with positive balance (optional - configurable)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountStatusChangeRule implements AccountBusinessRule {
    
    private AccountEntity account;
    private boolean activating;
    
    /**
     * Sets the account and operation type (activate/deactivate).
     */
    public AccountStatusChangeRule forAccount(AccountEntity account, boolean activating) {
        this.account = account;
        this.activating = activating;
        return this;
    }
    
    @Override
    public ValidationResult validate() {
        log.debug("Validating status change rule for account: {}, activating: {}", 
                account.getAccountNumber(), activating);
        
        if (account == null) {
            return ValidationResult.failure("Account is required", "ERR_ACCOUNT_NULL");
        }
        
        // Rule 1: Cannot activate if customer is inactive
        if (activating) {
            CustomerEntity customer = account.getCustomer();
            if (customer != null && !customer.getStatus()) {
                log.warn("Cannot activate account {} because customer {} is inactive", 
                        account.getAccountNumber(), customer.getCustomerCode());
                return ValidationResult.failure(
                        "Cannot activate account for inactive customer: " + customer.getCustomerCode(),
                        "ERR_CUSTOMER_INACTIVE"
                );
            }
        }
        
        // Rule 2: Cannot deactivate if account has positive balance
        if (!activating) {
            if (account.getInitialBalance() != null && 
                account.getInitialBalance().compareTo(BigDecimal.ZERO) > 0) {
                log.warn("Cannot deactivate account {} with positive balance: {}", 
                        account.getAccountNumber(), account.getInitialBalance());
                return ValidationResult.failure(
                        String.format("Cannot deactivate account with balance %s. Please withdraw funds first.", 
                                account.getInitialBalance()),
                        "ERR_ACCOUNT_HAS_BALANCE"
                );
            }
        }
        
        log.debug("Status change validation passed for account: {}", account.getAccountNumber());
        return ValidationResult.success();
    }
}
