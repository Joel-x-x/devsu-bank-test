package com.devsu.bank.movement.service;

import com.devsu.bank.infrastructure.pagination.FilterRequest;
import com.devsu.bank.infrastructure.pagination.PageResponse;
import com.devsu.bank.infrastructure.response.ResultResponse;
import com.devsu.bank.movement.dto.BalanceResponse;
import com.devsu.bank.movement.dto.MovementRequest;
import com.devsu.bank.movement.dto.MovementResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for Movement operations.
 * Movements are immutable - no update or delete operations.
 */
public interface MovementService {
    
    /**
     * Creates a new movement (transaction).
     * Validates business rules: account active, sufficient funds, daily limit.
     * 
     * @param request Movement creation request
     * @return ResultResponse containing the created movement
     */
    ResultResponse<MovementResponse, String> create(MovementRequest request);
    
    /**
     * Finds a movement by ID.
     * 
     * @param id Movement ID
     * @return ResultResponse containing the movement
     */
    ResultResponse<MovementResponse, String> findById(UUID id);
    
    /**
     * Finds all movements for a specific account within optional date range.
     * 
     * @param accountId Account ID (required)
     * @param startDate Optional start date for filtering
     * @param endDate Optional end date for filtering
     * @return ResultResponse containing list of movements
     */
    ResultResponse<List<MovementResponse>, String> findByAccountId(
            UUID accountId, 
            LocalDate startDate, 
            LocalDate endDate
    );
    
    /**
     * Calculates current balance and daily limit information for an account.
     * 
     * @param accountId Account ID
     * @return ResultResponse containing balance details
     */
    ResultResponse<BalanceResponse, String> calculateBalance(UUID accountId);
    
    /**
     * Calculates how much of the daily limit has been used today.
     * 
     * @param accountId Account ID
     * @return ResultResponse containing used amount
     */
    ResultResponse<BalanceResponse, String> calculateDailyLimitUsed(UUID accountId);
    
    /**
     * Finds movements with pagination and optional filtering.
     * 
     * @param filterRequest Filter and pagination parameters
     * @return ResultResponse containing paginated movement data
     */
    ResultResponse<PageResponse<MovementResponse>, String> findAllPaginated(FilterRequest filterRequest);
}
