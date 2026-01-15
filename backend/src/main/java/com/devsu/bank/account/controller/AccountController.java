package com.devsu.bank.account.controller;

import com.devsu.bank.account.dto.AccountRequest;
import com.devsu.bank.account.dto.AccountResponse;
import com.devsu.bank.account.dto.AccountUpdateRequest;
import com.devsu.bank.account.service.AccountService;
import com.devsu.bank.infrastructure.response.ResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for Account operations.
 * Provides endpoints for CRUD operations and account-specific queries.
 */
@Slf4j
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    
    private final AccountService accountService;
    
    /**
     * Creates a new account.
     * 
     * @param request The account creation request
     * @return ResponseEntity with the created account and 201 status
     */
    @PostMapping
    public ResponseEntity<ResultResponse<AccountResponse, String>> createAccount(
            @Valid @RequestBody AccountRequest request) {
        log.info("REST request to create account for customer: {}", request.customerId());
        ResultResponse<AccountResponse, String> response = accountService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Retrieves an account by ID.
     * 
     * @param id The account ID
     * @return ResponseEntity with the account data
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse<AccountResponse, String>> getAccountById(
            @PathVariable UUID id) {
        log.debug("REST request to get account by id: {}", id);
        ResultResponse<AccountResponse, String> response = accountService.findById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieves all accounts for a specific customer.
     * 
     * @param customerId The customer ID
     * @return ResponseEntity with list of customer accounts
     */
    @GetMapping
    public ResponseEntity<ResultResponse<List<AccountResponse>, String>> getAllAccounts(
            @RequestParam UUID customerId) {
        log.debug("REST request to get all accounts for customer: {}", customerId);
        ResultResponse<List<AccountResponse>, String> response = accountService.findByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Updates an account completely.
     * 
     * @param id The account ID
     * @param request The update request with all fields
     * @return ResponseEntity with the updated account
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResultResponse<AccountResponse, String>> updateAccount(
            @PathVariable UUID id,
            @Valid @RequestBody AccountUpdateRequest request) {
        log.info("REST request to update account: {}", id);
        ResultResponse<AccountResponse, String> response = accountService.update(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Partially updates an account (only provided fields).
     * 
     * @param id The account ID
     * @param request The partial update request
     * @return ResponseEntity with the updated account
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ResultResponse<AccountResponse, String>> partialUpdateAccount(
            @PathVariable UUID id,
            @RequestBody AccountUpdateRequest request) {
        log.info("REST request to partially update account: {}", id);
        ResultResponse<AccountResponse, String> response = accountService.partialUpdate(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Soft deletes an account.
     * 
     * @param id The account ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResultResponse<Void, String>> deleteAccount(@PathVariable UUID id) {
        log.info("REST request to delete account: {}", id);
        ResultResponse<Void, String> response = accountService.delete(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Permanently deletes an account from the database.
     * 
     * @param id The account ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<ResultResponse<Void, String>> hardDeleteAccount(@PathVariable UUID id) {
        log.warn("REST request to hard delete account: {}", id);
        ResultResponse<Void, String> response = accountService.hardDelete(id);
        return ResponseEntity.ok(response);
    }
    
    // ========== ACCOUNT-SPECIFIC ENDPOINTS ==========
    
    /**
     * Retrieves an account by its account number.
     * 
     * @param accountNumber The account number
     * @return ResponseEntity with the account data
     */
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<ResultResponse<AccountResponse, String>> getAccountByNumber(
            @PathVariable String accountNumber) {
        log.debug("REST request to get account by number: {}", accountNumber);
        ResultResponse<AccountResponse, String> response = accountService.findByAccountNumber(accountNumber);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieves all accounts for a specific customer.
     * 
     * @param customerId The customer ID
     * @return ResponseEntity with list of customer accounts
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ResultResponse<List<AccountResponse>, String>> getAccountsByCustomer(
            @PathVariable UUID customerId) {
        log.debug("REST request to get accounts by customer: {}", customerId);
        ResultResponse<List<AccountResponse>, String> response = accountService.findByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieves all active accounts.
     * 
     * @return ResponseEntity with list of active accounts
     */
    @GetMapping("/active")
    public ResponseEntity<ResultResponse<List<AccountResponse>, String>> getActiveAccounts() {
        log.debug("REST request to get active accounts");
        ResultResponse<List<AccountResponse>, String> response = accountService.findActiveAccounts();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieves all inactive accounts.
     * 
     * @return ResponseEntity with list of inactive accounts
     */
    @GetMapping("/inactive")
    public ResponseEntity<ResultResponse<List<AccountResponse>, String>> getInactiveAccounts() {
        log.debug("REST request to get inactive accounts");
        ResultResponse<List<AccountResponse>, String> response = accountService.findInactiveAccounts();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Activates an account.
     * 
     * @param id The account ID
     * @return ResponseEntity with the updated account
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ResultResponse<AccountResponse, String>> activateAccount(
            @PathVariable UUID id) {
        log.info("REST request to activate account: {}", id);
        ResultResponse<AccountResponse, String> response = accountService.activateAccount(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Deactivates an account.
     * 
     * @param id The account ID
     * @return ResponseEntity with the updated account
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ResultResponse<AccountResponse, String>> deactivateAccount(
            @PathVariable UUID id) {
        log.info("REST request to deactivate account: {}", id);
        ResultResponse<AccountResponse, String> response = accountService.deactivateAccount(id);
        return ResponseEntity.ok(response);
    }
}
