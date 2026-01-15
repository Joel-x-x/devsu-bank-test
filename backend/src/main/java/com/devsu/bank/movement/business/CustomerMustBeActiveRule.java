package com.devsu.bank.movement.business;

import com.devsu.bank.account.business.result.ValidationResult;
import com.devsu.bank.account.entity.AccountEntity;
import com.devsu.bank.customer.entity.CustomerEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Business rule: Customer of the account must be active to perform transactions.
 * Ensures movements are only created for accounts owned by active customers.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerMustBeActiveRule implements MovementBusinessRule {
    
    private AccountEntity account;
    
    /**
     * Sets the account (to check its customer) to validate.
     */
    public CustomerMustBeActiveRule forAccount(AccountEntity account) {
        this.account = account;
        return this;
    }
    
    @Override
    public ValidationResult validate() {
        log.debug("Validating customer active rule for account: {}", 
                account != null ? account.getAccountNumber() : "null");
        
        if (account == null) {
            return ValidationResult.failure("Account is required", "ERR_ACCOUNT_NULL");
        }
        
        CustomerEntity customer = account.getCustomer();
        if (customer == null) {
            return ValidationResult.failure("Account has no customer assigned", "ERR_NO_CUSTOMER");
        }
        
        if (!customer.getStatus()) {
            log.warn("Customer {} is inactive for account {}", 
                    customer.getCustomerCode(), account.getAccountNumber());
            return ValidationResult.failure(
                    "Cannot perform transaction for inactive customer: " + customer.getCustomerCode(),
                    "ERR_CUSTOMER_INACTIVE"
            );
        }
        
        log.debug("Customer {} is active", customer.getCustomerCode());
        return ValidationResult.success();
    }
}
