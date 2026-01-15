package com.devsu.bank.account.service;

import com.devsu.bank.account.dto.AccountRequest;
import com.devsu.bank.account.dto.AccountResponse;
import com.devsu.bank.account.dto.AccountUpdateRequest;
import com.devsu.bank.infrastructure.pagination.FilterRequest;
import com.devsu.bank.infrastructure.pagination.PageResponse;
import com.devsu.bank.infrastructure.response.ResultResponse;
import com.devsu.bank.infrastructure.service.CrudService;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Account operations.
 * Extends the generic CRUD operations and adds account-specific methods.
 * All methods return ResultResponse for consistent API responses.
 */
public interface AccountService extends CrudService<AccountRequest, AccountResponse, AccountUpdateRequest> {
    
    /**
     * Finds accounts with pagination and optional filtering.
     * 
     * @param filterRequest Filter and pagination parameters
     * @return ResultResponse containing paginated account data
     */
    ResultResponse<PageResponse<AccountResponse>, String> findAllPaginated(FilterRequest filterRequest);
    
    /**
     * Finds an account by its account number.
     * 
     * @param accountNumber The account number
     * @return ResultResponse containing the account data
     */
    ResultResponse<AccountResponse, String> findByAccountNumber(String accountNumber);
    
    /**
     * Retrieves all accounts for a specific customer.
     * 
     * @param customerId The customer ID
     * @return ResultResponse containing list of accounts
     */
    ResultResponse<List<AccountResponse>, String> findByCustomerId(UUID customerId);
    
    /**
     * Retrieves all active accounts.
     * 
     * @return ResultResponse containing list of active accounts
     */
    ResultResponse<List<AccountResponse>, String> findActiveAccounts();
    
    /**
     * Retrieves all inactive accounts.
     * 
     * @return ResultResponse containing list of inactive accounts
     */
    ResultResponse<List<AccountResponse>, String> findInactiveAccounts();
    
    /**
     * Activates an account.
     * 
     * @param id The account ID
     * @return ResultResponse containing the updated account
     */
    ResultResponse<AccountResponse, String> activateAccount(UUID id);
    
    /**
     * Deactivates an account.
     * 
     * @param id The account ID
     * @return ResultResponse containing the updated account
     */
    ResultResponse<AccountResponse, String> deactivateAccount(UUID id);
}