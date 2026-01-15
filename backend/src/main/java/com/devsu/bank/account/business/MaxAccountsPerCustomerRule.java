package com.devsu.bank.account.business;

import com.devsu.bank.account.AccountTypeEnum;
import com.devsu.bank.account.business.result.ValidationResult;
import com.devsu.bank.account.config.AccountBusinessConfig;
import com.devsu.bank.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Business rule: Customer cannot exceed maximum accounts per account type.
 * Prevents account proliferation and maintains manageable customer portfolios.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MaxAccountsPerCustomerRule implements AccountBusinessRule {
    
    private final AccountRepository accountRepository;
    private final AccountBusinessConfig config;
    
    private UUID customerId;
    private AccountTypeEnum accountType;
    
    /**
     * Sets the customer and account type to validate.
     */
    public MaxAccountsPerCustomerRule forCustomer(UUID customerId, AccountTypeEnum accountType) {
        this.customerId = customerId;
        this.accountType = accountType;
        return this;
    }
    
    @Override
    public ValidationResult validate() {
        log.debug("Validating max accounts rule for customer: {} and type: {}", customerId, accountType);
        
        if (customerId == null || accountType == null) {
            return ValidationResult.failure(
                    "Customer ID and account type are required",
                    "ERR_MISSING_PARAMS"
            );
        }
        
        long accountCount = accountRepository.countByCustomerIdAndAccountType(customerId, accountType);
        
        if (accountCount >= config.getMaxAccountsPerType()) {
            log.warn("Customer {} already has {} accounts of type {}", 
                    customerId, accountCount, accountType);
            return ValidationResult.failure(
                    String.format("Customer already has maximum allowed %s accounts of type %s", 
                            config.getMaxAccountsPerType(), accountType),
                    "ERR_MAX_ACCOUNTS_EXCEEDED"
            );
        }
        
        log.debug("Customer {} has {} accounts of type {}, limit is {}", 
                customerId, accountCount, accountType, config.getMaxAccountsPerType());
        return ValidationResult.success();
    }
}
