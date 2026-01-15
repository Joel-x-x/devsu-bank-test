package com.devsu.bank.movement.businessrule;

import com.devsu.bank.movement.business.DailyLimitRule;
import com.devsu.bank.account.business.result.ValidationResult;
import com.devsu.bank.account.entity.AccountEntity;
import com.devsu.bank.movement.entity.MovementTypeEnum;
import com.devsu.bank.movement.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyLimitRuleTest {

    @Mock
    private MovementRepository movementRepository;

    @InjectMocks
    private DailyLimitRule rule;

    private AccountEntity account;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        account = new AccountEntity();
        account.setId(accountId);
        account.setAccountNumber("1234567890");
        account.setDailyLimit(new BigDecimal("1000.00"));
    }

    @Test
    void validate_WhenCreditTransaction_ShouldAlwaysReturnSuccess() {
        // Arrange
        BigDecimal amount = new BigDecimal("500.00");

        // Act
        ValidationResult result = rule.forTransaction(
                account, 
                amount, 
                MovementTypeEnum.CREDIT
        ).validate();

        // Assert
        assertTrue(result.isValid());
    }

    @Test
    void validate_WhenDebitWithinLimit_ShouldReturnSuccess() {
        // Arrange
        BigDecimal amount = new BigDecimal("300.00");
        when(movementRepository.sumDebitsByAccountAndDateRange(any(), any(), any()))
                .thenReturn(new BigDecimal("200.00")); // Already used 200

        // Act
        ValidationResult result = rule.forTransaction(
                account, 
                amount, 
                MovementTypeEnum.DEBIT
        ).validate();

        // Assert
        assertTrue(result.isValid()); // 200 + 300 = 500 < 1000
    }

    @Test
    void validate_WhenDebitExceedsLimit_ShouldReturnFailure() {
        // Arrange
        BigDecimal amount = new BigDecimal("600.00");
        when(movementRepository.sumDebitsByAccountAndDateRange(any(), any(), any()))
                .thenReturn(new BigDecimal("500.00")); // Already used 500

        // Act
        ValidationResult result = rule.forTransaction(
                account, 
                amount, 
                MovementTypeEnum.DEBIT
        ).validate();

        // Assert
        assertFalse(result.isValid()); // 500 + 600 = 1100 > 1000
        assertNotNull(result.message());
    }

    @Test
    void validate_WhenDebitExactlyLimit_ShouldReturnSuccess() {
        // Arrange
        BigDecimal amount = new BigDecimal("500.00");
        when(movementRepository.sumDebitsByAccountAndDateRange(any(), any(), any()))
                .thenReturn(new BigDecimal("500.00")); // Already used 500

        // Act
        ValidationResult result = rule.forTransaction(
                account, 
                amount, 
                MovementTypeEnum.DEBIT
        ).validate();

        // Assert
        assertTrue(result.isValid()); // 500 + 500 = 1000 == 1000
    }

    @Test
    void validate_WhenNoDebitsToday_ShouldReturnSuccess() {
        // Arrange
        BigDecimal amount = new BigDecimal("999.00");
        when(movementRepository.sumDebitsByAccountAndDateRange(any(), any(), any()))
                .thenReturn(BigDecimal.ZERO); // No debits today

        // Act
        ValidationResult result = rule.forTransaction(
                account, 
                amount, 
                MovementTypeEnum.DEBIT
        ).validate();

        // Assert
        assertTrue(result.isValid()); // 0 + 999 = 999 < 1000
    }
}
