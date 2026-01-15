package com.devsu.bank.account.service;

import com.devsu.bank.account.business.AccountStatusChangeRule;
import com.devsu.bank.account.business.ActiveCustomerRule;
import com.devsu.bank.account.business.MaxAccountsPerCustomerRule;
import com.devsu.bank.account.business.result.ValidationResult;
import com.devsu.bank.account.config.AccountBusinessConfig;
import com.devsu.bank.account.dto.AccountRequest;
import com.devsu.bank.account.dto.AccountResponse;
import com.devsu.bank.account.dto.AccountUpdateRequest;
import com.devsu.bank.account.entity.AccountEntity;
import com.devsu.bank.account.mapper.AccountMapper;
import com.devsu.bank.account.repository.AccountRepository;
import com.devsu.bank.customer.entity.CustomerEntity;
import com.devsu.bank.customer.repository.CustomerRepository;
import com.devsu.bank.infrastructure.exception.EntityAlreadyExistsException;
import com.devsu.bank.infrastructure.exception.EntityNotFoundException;
import com.devsu.bank.infrastructure.exception.InvalidOperationException;
import com.devsu.bank.infrastructure.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of AccountService.
 * Provides business logic for account operations including CRUD and account-specific methods.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {
    
    private final AccountRepository repository;
    private final AccountMapper mapper;
    private final CustomerRepository customerRepository;
    
    // Business Rules
    private final ActiveCustomerRule activeCustomerRule;
    private final MaxAccountsPerCustomerRule maxAccountsPerCustomerRule;
    private final AccountStatusChangeRule accountStatusChangeRule;
    
    // Configuration
    private final AccountBusinessConfig config;
    
    // ========== CRUD OPERATIONS ==========
    
    @Override
    public ResultResponse<AccountResponse, String> create(AccountRequest request) {
        log.info("Creating account for customer: {}", request.customerId());
        
        // Business Rule 1: Validate customer is active
        ValidationResult customerValidation = activeCustomerRule
                .forCustomer(request.customerId())
                .validate();
        if (!customerValidation.isValid()) {
            throw new InvalidOperationException(customerValidation.message());
        }
        
        // Fetch customer entity
        CustomerEntity customer = customerRepository.findById(request.customerId())
            .orElseThrow(() -> new EntityNotFoundException("Customer", request.customerId()));
        
        // Simple validation: Daily limit must be within valid range
        if (request.dailyLimit() == null) {
            throw new InvalidOperationException("Daily limit is required");
        }
        if (request.dailyLimit().compareTo(config.getMinDailyLimit()) < 0) {
            throw new InvalidOperationException(
                String.format("Daily limit must be at least %s", config.getMinDailyLimit())
            );
        }
        if (request.dailyLimit().compareTo(config.getMaxDailyLimit()) > 0) {
            throw new InvalidOperationException(
                String.format("Daily limit cannot exceed %s", config.getMaxDailyLimit())
            );
        }
        
        // Business Rule 2: Validate max accounts per type
        ValidationResult maxAccountsValidation = maxAccountsPerCustomerRule
                .forCustomer(request.customerId(), request.accountType())
                .validate();
        if (!maxAccountsValidation.isValid()) {
            throw new InvalidOperationException(maxAccountsValidation.message());
        }
        
        // Simple validation: Initial balance cannot be negative
        if (request.initialBalance() != null && 
            request.initialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOperationException("Initial balance cannot be negative");
        }
        
        AccountEntity entity = mapper.toEntity(request);
        entity.setCustomer(customer);
        
        // Set default status if not provided
        if (entity.getStatus() == null) {
            entity.setStatus(true);
        }
        
        // Set initial balance to 0 if not provided
        if (entity.getInitialBalance() == null) {
            entity.setInitialBalance(BigDecimal.ZERO);
        }
        
        AccountEntity saved = repository.save(entity);
        AccountResponse response = mapper.toResponse(saved);
        
        log.info("Account created successfully with number: {}", saved.getAccountNumber());
        return ResultResponse.created(response, "Account created successfully");
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<AccountResponse, String> findById(UUID id) {
        log.debug("Finding account by id: {}", id);
        
        AccountResponse response = repository.findById(id)
            .filter(account -> account.getDeletedAt() == null)
            .map(mapper::toResponse)
            .orElseThrow(() -> new EntityNotFoundException("Account", id));
        
        return ResultResponse.success(response);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<List<AccountResponse>, String> findAll() {
        log.debug("Finding all accounts");
        
        List<AccountResponse> accounts = repository.findAll().stream()
            .filter(account -> account.getDeletedAt() == null)
            .map(mapper::toResponse)
            .toList();
        
        return ResultResponse.success(accounts);
    }
    
    @Override
    public ResultResponse<AccountResponse, String> update(UUID id, AccountUpdateRequest request) {
        log.info("Updating account with id: {}", id);
        
        AccountEntity entity = findEntityById(id);
        
        // Update fields
        mapper.updateEntityFromDto(request, entity);
        
        AccountEntity updated = repository.save(entity);
        AccountResponse response = mapper.toResponse(updated);
        
        log.info("Account updated successfully: {}", id);
        return ResultResponse.updated(response);
    }
    
    @Override
    public ResultResponse<AccountResponse, String> partialUpdate(UUID id, AccountUpdateRequest request) {
        log.info("Partial update for account with id: {}", id);
        
        // The mapper is already configured to ignore null values
        return update(id, request);
    }
    
    @Override
    public ResultResponse<Void, String> delete(UUID id) {
        log.info("Soft deleting account with id: {}", id);
        
        AccountEntity entity = findEntityById(id);
        
        entity.softDelete("SYSTEM");
        entity.setStatus(false);
        repository.save(entity);
        
        log.info("Account soft deleted successfully: {}", id);
        return ResultResponse.deleted();
    }
    
    @Override
    public ResultResponse<Void, String> hardDelete(UUID id) {
        log.warn("Hard deleting account with id: {}", id);
        
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Account", id);
        }
        
        repository.deleteById(id);
        log.info("Account hard deleted successfully: {}", id);
        return ResultResponse.deleted();
    }
    
    // ========== ACCOUNT-SPECIFIC OPERATIONS ==========
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<AccountResponse, String> findByAccountNumber(String accountNumber) {
        log.debug("Finding account by account number: {}", accountNumber);
        
        AccountResponse response = repository.findByAccountNumber(accountNumber)
            .filter(account -> account.getDeletedAt() == null)
            .map(mapper::toResponse)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Account not found with number: %s", accountNumber)
            ));
        
        return ResultResponse.success(response);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<List<AccountResponse>, String> findByCustomerId(UUID customerId) {
        log.debug("Finding accounts by customer id: {}", customerId);
        
        List<AccountResponse> accounts = repository.findByCustomerId(customerId).stream()
            .filter(account -> account.getDeletedAt() == null)
            .map(mapper::toResponse)
            .toList();
        
        return ResultResponse.success(accounts);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<List<AccountResponse>, String> findActiveAccounts() {
        log.debug("Finding active accounts");
        
        List<AccountResponse> accounts = repository.findAll().stream()
            .filter(account -> account.getDeletedAt() == null)
            .filter(account -> Boolean.TRUE.equals(account.getStatus()))
            .map(mapper::toResponse)
            .toList();
        
        return ResultResponse.success(accounts);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<List<AccountResponse>, String> findInactiveAccounts() {
        log.debug("Finding inactive accounts");
        
        List<AccountResponse> accounts = repository.findAll().stream()
            .filter(account -> account.getDeletedAt() == null)
            .filter(account -> Boolean.FALSE.equals(account.getStatus()))
            .map(mapper::toResponse)
            .toList();
        
        return ResultResponse.success(accounts);
    }
    
    @Override
    public ResultResponse<AccountResponse, String> activateAccount(UUID id) {
        log.info("Activating account: {}", id);
        
        AccountEntity entity = findEntityById(id);
        
        // Business Rule: Validate account can be activated
        ValidationResult validation = accountStatusChangeRule
                .forAccount(entity, true)
                .validate();
        if (!validation.isValid()) {
            throw new InvalidOperationException(validation.message());
        }
        
        entity.setStatus(true);
        
        AccountEntity updated = repository.save(entity);
        AccountResponse response = mapper.toResponse(updated);
        
        log.info("Account activated successfully: {}", id);
        return ResultResponse.updated(response);
    }
    
    @Override
    public ResultResponse<AccountResponse, String> deactivateAccount(UUID id) {
        log.info("Deactivating account: {}", id);
        
        AccountEntity entity = findEntityById(id);
        
        // Business Rule: Validate account can be deactivated
        ValidationResult validation = accountStatusChangeRule
                .forAccount(entity, false)
                .validate();
        if (!validation.isValid()) {
            throw new InvalidOperationException(validation.message());
        }
        
        entity.setStatus(false);
        
        AccountEntity updated = repository.save(entity);
        AccountResponse response = mapper.toResponse(updated);
        
        log.info("Account deactivated successfully: {}", id);
        return ResultResponse.updated(response);
    }
    
    // ========== PRIVATE HELPER METHODS ==========
    
    private AccountEntity findEntityById(UUID id) {
        return repository.findById(id)
            .filter(account -> account.getDeletedAt() == null)
            .orElseThrow(() -> new EntityNotFoundException("Account", id));
    }
}
