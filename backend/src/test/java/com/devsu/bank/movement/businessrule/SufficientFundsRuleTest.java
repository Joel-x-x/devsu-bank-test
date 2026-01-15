package com.devsu.bank.movement.businessrule;

import com.devsu.bank.movement.business.SufficientFundsRule;
import com.devsu.bank.account.entity.AccountEntity;
import com.devsu.bank.movement.entity.MovementTypeEnum;
import com.devsu.bank.account.business.result.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SufficientFundsRuleTest {

    @InjectMocks
    private SufficientFundsRule rule;

    private AccountEntity account;

    @BeforeEach
    void setUp() {
        account = new AccountEntity();
        account.setAccountNumber("1234567890");
    }

    @Test
    void validate_WhenCreditTransaction_ShouldAlwaysReturnSuccess() {
        // Arrange
        BigDecimal currentBalance = new BigDecimal("100.00");
        BigDecimal amount = new BigDecimal("50.00");

        // Act
        ValidationResult result = rule.forTransaction(
                account, 
                amount, 
                currentBalance, 
                MovementTypeEnum.CREDIT
        ).validate();

        // Assert
        assertTrue(result.isValid());
    }

    @Test
    void validate_WhenDebitWithSufficientFunds_ShouldReturnSuccess() {
        // Arrange
        BigDecimal currentBalance = new BigDecimal("1000.00");
        BigDecimal amount = new BigDecimal("500.00");

        // Act
        ValidationResult result = rule.forTransaction(
                account, 
                amount, 
                currentBalance, 
                MovementTypeEnum.DEBIT
        ).validate();

        // Assert
        assertTrue(result.isValid());
    }

    @Test
    void validate_WhenDebitWithInsufficientFunds_ShouldReturnFailure() {
        // Arrange
        BigDecimal currentBalance = new BigDecimal("100.00");
        BigDecimal amount = new BigDecimal("500.00");

        // Act
        ValidationResult result = rule.forTransaction(
                account, 
                amount, 
                currentBalance, 
                MovementTypeEnum.DEBIT
        ).validate();

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.message().contains("Insufficient funds"));
    }

    @Test
    void validate_WhenDebitExactBalance_ShouldReturnSuccess() {
        // Arrange
        BigDecimal currentBalance = new BigDecimal("500.00");
        BigDecimal amount = new BigDecimal("500.00");

        // Act
        ValidationResult result = rule.forTransaction(
                account, 
                amount, 
                currentBalance, 
                MovementTypeEnum.DEBIT
        ).validate();

        // Assert
        assertTrue(result.isValid());
    }
}
