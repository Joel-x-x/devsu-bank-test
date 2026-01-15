package com.devsu.bank.movement.businessrule;

import com.devsu.bank.movement.business.AccountMustBeActiveRule;
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
class AccountMustBeActiveRuleTest {

    @InjectMocks
    private AccountMustBeActiveRule rule;

    private AccountEntity account;

    @BeforeEach
    void setUp() {
        account = new AccountEntity();
    }

    @Test
    void validate_WhenAccountIsActive_ShouldReturnSuccess() {
        // Arrange
        account.setStatus(true);

        // Act
        ValidationResult result = rule.forAccount(account).validate();

        // Assert
        assertTrue(result.isValid());
    }

    @Test
    void validate_WhenAccountIsInactive_ShouldReturnFailure() {
        // Arrange
        account.setStatus(false);

        // Act
        ValidationResult result = rule.forAccount(account).validate();

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.message().contains("inactive"));
    }
}
