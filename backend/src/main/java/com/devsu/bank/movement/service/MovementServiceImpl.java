package com.devsu.bank.movement.service;

import com.devsu.bank.account.business.result.ValidationResult;
import com.devsu.bank.account.entity.AccountEntity;
import com.devsu.bank.account.repository.AccountRepository;
import com.devsu.bank.infrastructure.exception.EntityNotFoundException;
import com.devsu.bank.infrastructure.exception.InvalidOperationException;
import com.devsu.bank.infrastructure.response.ResultResponse;
import com.devsu.bank.movement.business.*;
import com.devsu.bank.movement.dto.BalanceResponse;
import com.devsu.bank.movement.dto.MovementRequest;
import com.devsu.bank.movement.dto.MovementResponse;
import com.devsu.bank.movement.entity.MovementEntity;
import com.devsu.bank.movement.entity.MovementTypeEnum;
import com.devsu.bank.movement.mapper.MovementMapper;
import com.devsu.bank.movement.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of MovementService.
 * Handles transaction operations with strict validation and concurrency control.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {
    
    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;
    private final MovementMapper mapper;
    
    // Business Rules
    private final AccountMustBeActiveRule accountActiveRule;
    private final CustomerMustBeActiveRule customerActiveRule;
    private final SufficientFundsRule sufficientFundsRule;
    private final DailyLimitRule dailyLimitRule;
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResultResponse<MovementResponse, String> create(MovementRequest request) {
        log.info("Creating movement for account: {}, type: {}, amount: {}", 
                request.accountId(), request.movementType(), request.amount());
        
        // Fetch account with customer
        AccountEntity account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new EntityNotFoundException("Account", request.accountId()));
        
        // Business Rule 1: Account must be active
        ValidationResult accountValidation = accountActiveRule
                .forAccount(account)
                .validate();
        if (!accountValidation.isValid()) {
            throw new InvalidOperationException(accountValidation.message());
        }
        
        // Business Rule 2: Customer must be active
        ValidationResult customerValidation = customerActiveRule
                .forAccount(account)
                .validate();
        if (!customerValidation.isValid()) {
            throw new InvalidOperationException(customerValidation.message());
        }
        
        // Calculate current balance
        BigDecimal currentBalance = calculateCurrentBalance(account);
        
        // Business Rule 3: Sufficient funds for debit
        ValidationResult fundsValidation = sufficientFundsRule
                .forTransaction(account, request.amount(), currentBalance, request.movementType())
                .validate();
        if (!fundsValidation.isValid()) {
            throw new InvalidOperationException(fundsValidation.message());
        }
        
        // Business Rule 4: Daily limit for debit
        ValidationResult limitValidation = dailyLimitRule
                .forTransaction(account, request.amount(), request.movementType())
                .validate();
        if (!limitValidation.isValid()) {
            throw new InvalidOperationException(limitValidation.message());
        }
        
        // Calculate new balance
        BigDecimal newBalance = request.movementType() == MovementTypeEnum.CREDIT
                ? currentBalance.add(request.amount())
                : currentBalance.subtract(request.amount());
        
        // Calculate daily limit used after this transaction
        BigDecimal dailyLimitUsedAfter = request.movementType() == MovementTypeEnum.DEBIT
                ? calculateDailyLimitUsedInternal(account.getId()).add(request.amount())
                : calculateDailyLimitUsedInternal(account.getId());
        
        // Calculate available balance: min(current balance, daily limit remaining)
        BigDecimal dailyLimitRemaining = account.getDailyLimit().subtract(dailyLimitUsedAfter);
        BigDecimal availableBalance = newBalance.min(dailyLimitRemaining).max(BigDecimal.ZERO);
        
        // Create movement entity
        MovementEntity entity = mapper.toEntity(request);
        entity.setAccount(account);
        entity.setBalance(newBalance);
        entity.setAvailableBalance(availableBalance);
        entity.setMovementDate(LocalDateTime.now());
        
        // Save
        MovementEntity saved = movementRepository.save(entity);
        MovementResponse response = mapper.toResponse(saved);
        
        log.info("Movement created successfully. New balance: {}", newBalance);
        return ResultResponse.created(response, "Movement created successfully");
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<MovementResponse, String> findById(UUID id) {
        log.debug("Finding movement by id: {}", id);
        
        MovementEntity movement = movementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movement", id));
        
        MovementResponse response = mapper.toResponse(movement);
        return ResultResponse.success(response);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<List<MovementResponse>, String> findByAccountId(
            UUID accountId, 
            LocalDate startDate, 
            LocalDate endDate) {
        log.debug("Finding movements for account: {}, from: {}, to: {}", 
                accountId, startDate, endDate);
        
        // Validate account exists
        accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account", accountId));
        
        List<MovementEntity> movements;
        
        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            movements = movementRepository.findByAccountIdAndMovementDateBetweenOrderByMovementDateDesc(accountId, start, end);
        } else {
            movements = movementRepository.findByAccountIdOrderByMovementDateDesc(accountId);
        }
        
        List<MovementResponse> responses = movements.stream()
                .map(mapper::toResponse)
                .toList();
        
        return ResultResponse.success(responses);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<BalanceResponse, String> calculateBalance(UUID accountId) {
        log.debug("Calculating balance for account: {}", accountId);
        
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account", accountId));
        
        BigDecimal currentBalance = calculateCurrentBalance(account);
        BigDecimal dailyLimitUsed = calculateDailyLimitUsedInternal(accountId);
        BigDecimal dailyLimitRemaining = account.getDailyLimit().subtract(dailyLimitUsed);
        
        BalanceResponse response = new BalanceResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getInitialBalance(),
                currentBalance,
                account.getDailyLimit(),
                dailyLimitUsed,
                dailyLimitRemaining.max(BigDecimal.ZERO)
        );
        
        return ResultResponse.success(response);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<BalanceResponse, String> calculateDailyLimitUsed(UUID accountId) {
        log.debug("Calculating daily limit used for account: {}", accountId);
        
        return calculateBalance(accountId);
    }
    
    // ========== PRIVATE HELPER METHODS ==========
    
    /**
     * Calculates current balance: initial balance + sum of movements.
     */
    private BigDecimal calculateCurrentBalance(AccountEntity account) {
        BigDecimal movementsBalance = movementRepository.calculateBalanceByAccount(account.getId());
        return account.getInitialBalance().add(movementsBalance);
    }
    
    /**
     * Calculates how much of daily limit has been used today.
     */
    private BigDecimal calculateDailyLimitUsedInternal(UUID accountId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        
        BigDecimal used = movementRepository.sumDebitsByAccountAndDateRange(
                accountId, startOfDay, endOfDay);
        
        return used != null ? used : BigDecimal.ZERO;
    }
}
