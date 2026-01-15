package com.devsu.bank.movement.businessrule;

import com.devsu.bank.movement.business.CustomerMustBeActiveRule;
import com.devsu.bank.account.business.result.ValidationResult;
import com.devsu.bank.account.entity.AccountEntity;
import com.devsu.bank.customer.entity.CustomerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerMustBeActiveRuleTest {

    @InjectMocks
    private CustomerMustBeActiveRule rule;

    private AccountEntity account;
    private CustomerEntity customer;

    @BeforeEach
    void setUp() {
        customer = new CustomerEntity();
        account = new AccountEntity();
        account.setCustomer(customer);
    }

    @Test
    void validate_WhenCustomerIsActive_ShouldReturnSuccess() {
        // Arrange
        customer.setStatus(true);

        // Act
        ValidationResult result = rule.forAccount(account).validate();

        // Assert
        assertTrue(result.isValid());
    }

    @Test
    void validate_WhenCustomerIsInactive_ShouldReturnFailure() {
        // Arrange
        customer.setStatus(false);

        // Act
        ValidationResult result = rule.forAccount(account).validate();

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.message().contains("inactive"));
    }
}
