package com.devsu.bank.account.business;

import com.devsu.bank.account.business.result.ValidationResult;
import com.devsu.bank.customer.entity.CustomerEntity;
import com.devsu.bank.customer.repository.CustomerRepository;
import com.devsu.bank.infrastructure.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Business rule: Customer must exist and be active to create/update accounts.
 * Ensures accounts are only created for valid, active customers.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActiveCustomerRule implements AccountBusinessRule {
    
    private final CustomerRepository customerRepository;
    private UUID customerId;
    
    /**
     * Sets the customer ID to validate.
     */
    public ActiveCustomerRule forCustomer(UUID customerId) {
        this.customerId = customerId;
        return this;
    }
    
    @Override
    public ValidationResult validate() {
        log.debug("Validating active customer rule for customer: {}", customerId);
        
        if (customerId == null) {
            return ValidationResult.failure("Customer ID is required", "ERR_CUSTOMER_ID_NULL");
        }
        
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer", customerId));
        
        if (!customer.getStatus()) {
            log.warn("Customer {} is inactive", customerId);
            return ValidationResult.failure(
                    "Cannot create account for inactive customer: " + customer.getCustomerCode(),
                    "ERR_CUSTOMER_INACTIVE"
            );
        }
        
        log.debug("Customer {} is active and valid", customerId);
        return ValidationResult.success();
    }
}
