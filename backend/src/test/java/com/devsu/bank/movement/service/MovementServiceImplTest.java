package com.devsu.bank.movement.service;

import com.devsu.bank.account.entity.AccountEntity;
import com.devsu.bank.account.AccountTypeEnum;
import com.devsu.bank.account.repository.AccountRepository;
import com.devsu.bank.customer.entity.CustomerEntity;
import com.devsu.bank.infrastructure.exception.EntityNotFoundException;
import com.devsu.bank.infrastructure.exception.InvalidOperationException;
import com.devsu.bank.infrastructure.response.ResultResponse;
import com.devsu.bank.movement.business.AccountMustBeActiveRule;
import com.devsu.bank.movement.business.CustomerMustBeActiveRule;
import com.devsu.bank.movement.business.DailyLimitRule;
import com.devsu.bank.movement.business.SufficientFundsRule;
import com.devsu.bank.movement.dto.BalanceResponse;
import com.devsu.bank.movement.dto.MovementRequest;
import com.devsu.bank.movement.dto.MovementResponse;
import com.devsu.bank.movement.entity.MovementEntity;
import com.devsu.bank.movement.entity.MovementTypeEnum;
import com.devsu.bank.movement.mapper.MovementMapper;
import com.devsu.bank.movement.repository.MovementRepository;
import com.devsu.bank.account.business.result.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementServiceImplTest {

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MovementMapper mapper;

    @Mock
    private AccountMustBeActiveRule accountMustBeActiveRule;

    @Mock
    private CustomerMustBeActiveRule customerMustBeActiveRule;

    @Mock
    private SufficientFundsRule sufficientFundsRule;

    @Mock
    private DailyLimitRule dailyLimitRule;

    @InjectMocks
    private MovementServiceImpl movementService;

    private UUID accountId;
    private UUID customerId;
    private UUID movementId;
    private AccountEntity account;
    private CustomerEntity customer;
    private MovementRequest creditRequest;
    private MovementRequest debitRequest;
    private MovementEntity movementEntity;
    private MovementResponse movementResponse;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        movementId = UUID.randomUUID();

        // Setup customer
        customer = new CustomerEntity();
        customer.setId(customerId);
        customer.setName("Test Customer");
        customer.setStatus(true);

        // Setup account
        account = new AccountEntity();
        account.setId(accountId);
        account.setAccountNumber("1234567890");
        account.setAccountType(AccountTypeEnum.SAVINGS);
        account.setInitialBalance(new BigDecimal("1000.00"));
        account.setDailyLimit(new BigDecimal("500.00"));
        account.setStatus(true);
        account.setCustomer(customer);

        // Setup requests
        creditRequest = new MovementRequest(
                MovementTypeEnum.CREDIT,
                new BigDecimal("100.00"),
                accountId
        );

        debitRequest = new MovementRequest(
                MovementTypeEnum.DEBIT,
                new BigDecimal("50.00"),
                accountId
        );

        // Setup entity
        movementEntity = new MovementEntity();
        movementEntity.setId(movementId);
        movementEntity.setMovementType(MovementTypeEnum.CREDIT);
        movementEntity.setAmount(new BigDecimal("100.00"));
        movementEntity.setBalance(new BigDecimal("1100.00"));
        movementEntity.setAvailableBalance(new BigDecimal("1100.00"));
        movementEntity.setAccount(account);
        movementEntity.setMovementDate(LocalDateTime.now());

        // Setup response
        movementResponse = new MovementResponse(
                movementId,
                LocalDateTime.now(),
                MovementTypeEnum.CREDIT,
                new BigDecimal("100.00"),
                new BigDecimal("1100.00"),
                new BigDecimal("1100.00"),
                accountId,
                "1234567890",
                LocalDateTime.now()
        );
    }

    @Test
    void create_WhenCreditTransactionValid_ShouldCreateMovement() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMustBeActiveRule.forAccount(account)).thenReturn(accountMustBeActiveRule);
        when(accountMustBeActiveRule.validate()).thenReturn(ValidationResult.success());
        when(customerMustBeActiveRule.forAccount(account)).thenReturn(customerMustBeActiveRule);
        when(customerMustBeActiveRule.validate()).thenReturn(ValidationResult.success());
        when(sufficientFundsRule.forTransaction(any(), any(), any(), any())).thenReturn(sufficientFundsRule);
        when(sufficientFundsRule.validate()).thenReturn(ValidationResult.success());
        when(dailyLimitRule.forTransaction(any(), any(), any())).thenReturn(dailyLimitRule);
        when(dailyLimitRule.validate()).thenReturn(ValidationResult.success());
        when(movementRepository.calculateBalanceByAccount(accountId)).thenReturn(BigDecimal.ZERO);
        when(movementRepository.sumDebitsByAccountAndDateRange(any(), any(), any())).thenReturn(BigDecimal.ZERO);
        when(mapper.toEntity(creditRequest)).thenReturn(movementEntity);
        when(movementRepository.save(any(MovementEntity.class))).thenReturn(movementEntity);
        when(mapper.toResponse(movementEntity)).thenReturn(movementResponse);

        // Act
        ResultResponse<MovementResponse, String> result = movementService.create(creditRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(201, result.getCode());
        assertNotNull(result.getResult());
        assertEquals(MovementTypeEnum.CREDIT, result.getResult().movementType());
        verify(movementRepository).save(any(MovementEntity.class));
    }

    @Test
    void create_WhenDebitTransactionValid_ShouldCreateMovement() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMustBeActiveRule.forAccount(account)).thenReturn(accountMustBeActiveRule);
        when(accountMustBeActiveRule.validate()).thenReturn(ValidationResult.success());
        when(customerMustBeActiveRule.forAccount(account)).thenReturn(customerMustBeActiveRule);
        when(customerMustBeActiveRule.validate()).thenReturn(ValidationResult.success());
        when(sufficientFundsRule.forTransaction(any(), any(), any(), any())).thenReturn(sufficientFundsRule);
        when(sufficientFundsRule.validate()).thenReturn(ValidationResult.success());
        when(dailyLimitRule.forTransaction(any(), any(), any())).thenReturn(dailyLimitRule);
        when(dailyLimitRule.validate()).thenReturn(ValidationResult.success());
        when(movementRepository.calculateBalanceByAccount(accountId)).thenReturn(BigDecimal.ZERO);
        when(movementRepository.sumDebitsByAccountAndDateRange(any(), any(), any())).thenReturn(BigDecimal.ZERO);
        when(mapper.toEntity(debitRequest)).thenReturn(movementEntity);
        when(movementRepository.save(any(MovementEntity.class))).thenReturn(movementEntity);
        when(mapper.toResponse(movementEntity)).thenReturn(movementResponse);

        // Act
        ResultResponse<MovementResponse, String> result = movementService.create(debitRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(movementRepository).save(any(MovementEntity.class));
    }

    @Test
    void create_WhenAccountNotFound_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> movementService.create(creditRequest));
        verify(movementRepository, never()).save(any());
    }

    @Test
    void create_WhenAccountInactive_ShouldThrowException() {
        // Arrange
        account.setStatus(false);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMustBeActiveRule.forAccount(account)).thenReturn(accountMustBeActiveRule);
        when(accountMustBeActiveRule.validate()).thenReturn(ValidationResult.failure("Account is inactive"));

        // Act & Assert
        assertThrows(InvalidOperationException.class, () -> movementService.create(creditRequest));
        verify(movementRepository, never()).save(any());
    }

    @Test
    void create_WhenCustomerInactive_ShouldThrowException() {
        // Arrange
        customer.setStatus(false);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMustBeActiveRule.forAccount(account)).thenReturn(accountMustBeActiveRule);
        when(accountMustBeActiveRule.validate()).thenReturn(ValidationResult.success());
        when(customerMustBeActiveRule.forAccount(account)).thenReturn(customerMustBeActiveRule);
        when(customerMustBeActiveRule.validate()).thenReturn(ValidationResult.failure("Customer is inactive"));

        // Act & Assert
        assertThrows(InvalidOperationException.class, () -> movementService.create(creditRequest));
        verify(movementRepository, never()).save(any());
    }

    @Test
    void create_WhenInsufficientFunds_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMustBeActiveRule.forAccount(account)).thenReturn(accountMustBeActiveRule);
        when(accountMustBeActiveRule.validate()).thenReturn(ValidationResult.success());
        when(customerMustBeActiveRule.forAccount(account)).thenReturn(customerMustBeActiveRule);
        when(customerMustBeActiveRule.validate()).thenReturn(ValidationResult.success());
        when(movementRepository.calculateBalanceByAccount(accountId)).thenReturn(new BigDecimal("10.00"));
        when(sufficientFundsRule.forTransaction(any(), any(), any(), any())).thenReturn(sufficientFundsRule);
        when(sufficientFundsRule.validate()).thenReturn(ValidationResult.failure("Insufficient funds"));

        // Act & Assert
        assertThrows(InvalidOperationException.class, () -> movementService.create(debitRequest));
        verify(movementRepository, never()).save(any());
    }

    @Test
    void create_WhenDailyLimitExceeded_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMustBeActiveRule.forAccount(account)).thenReturn(accountMustBeActiveRule);
        when(accountMustBeActiveRule.validate()).thenReturn(ValidationResult.success());
        when(customerMustBeActiveRule.forAccount(account)).thenReturn(customerMustBeActiveRule);
        when(customerMustBeActiveRule.validate()).thenReturn(ValidationResult.success());
        when(movementRepository.calculateBalanceByAccount(accountId)).thenReturn(new BigDecimal("1000.00"));
        when(sufficientFundsRule.forTransaction(any(), any(), any(), any())).thenReturn(sufficientFundsRule);
        when(sufficientFundsRule.validate()).thenReturn(ValidationResult.success());
        when(dailyLimitRule.forTransaction(any(), any(), any())).thenReturn(dailyLimitRule);
        when(dailyLimitRule.validate()).thenReturn(ValidationResult.failure("Daily limit exceeded"));

        // Act & Assert
        assertThrows(InvalidOperationException.class, () -> movementService.create(debitRequest));
        verify(movementRepository, never()).save(any());
    }

    @Test
    void findById_WhenMovementExists_ShouldReturnMovement() {
        // Arrange
        when(movementRepository.findById(movementId)).thenReturn(Optional.of(movementEntity));
        when(mapper.toResponse(movementEntity)).thenReturn(movementResponse);

        // Act
        ResultResponse<MovementResponse, String> result = movementService.findById(movementId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(movementId, result.getResult().id());
        verify(movementRepository).findById(movementId);
    }

    @Test
    void findById_WhenMovementNotFound_ShouldThrowException() {
        // Arrange
        when(movementRepository.findById(movementId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> movementService.findById(movementId));
    }

    @Test
    void findByAccountId_WhenAccountExists_ShouldReturnMovements() {
        // Arrange
        List<MovementEntity> movements = List.of(movementEntity);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(movementRepository.findByAccountIdOrderByMovementDateDesc(accountId)).thenReturn(movements);
        when(mapper.toResponse(movementEntity)).thenReturn(movementResponse);

        // Act
        ResultResponse<List<MovementResponse>, String> result = movementService.findByAccountId(accountId, null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertFalse(result.getResult().isEmpty());
        assertEquals(1, result.getResult().size());
        verify(movementRepository).findByAccountIdOrderByMovementDateDesc(accountId);
    }

    @Test
    void calculateBalance_WhenAccountExists_ShouldReturnBalance() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(movementRepository.calculateBalanceByAccount(accountId)).thenReturn(BigDecimal.ZERO);
        when(movementRepository.sumDebitsByAccountAndDateRange(any(), any(), any())).thenReturn(BigDecimal.ZERO);

        // Act
        ResultResponse<BalanceResponse, String> result = movementService.calculateBalance(accountId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getResult());
        assertEquals(accountId, result.getResult().accountId());
    }

    @Test
    void calculateBalance_WhenAccountNotFound_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> movementService.calculateBalance(accountId));
    }
}
