package com.devsu.bank.movement.controller;

import com.devsu.bank.infrastructure.response.ResultResponse;
import com.devsu.bank.movement.dto.BalanceResponse;
import com.devsu.bank.movement.dto.MovementRequest;
import com.devsu.bank.movement.dto.MovementResponse;
import com.devsu.bank.movement.service.MovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for Movement operations.
 * Movements are immutable - only create and read operations are supported.
 */
@Slf4j
@RestController
@RequestMapping("/movements")
@RequiredArgsConstructor
public class MovementController {
    
    private final MovementService movementService;
    
    /**
     * Creates a new movement (transaction).
     * Validates all business rules before creating the transaction.
     * 
     * @param request The movement creation request
     * @return ResponseEntity with the created movement and 201 status
     */
    @PostMapping
    public ResponseEntity<ResultResponse<MovementResponse, String>> createMovement(
            @Valid @RequestBody MovementRequest request) {
        log.info("REST request to create movement for account: {}, type: {}", 
                request.accountId(), request.movementType());
        ResultResponse<MovementResponse, String> response = movementService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Retrieves a movement by ID.
     * 
     * @param id The movement ID
     * @return ResponseEntity with the movement data
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse<MovementResponse, String>> getMovementById(
            @PathVariable UUID id) {
        log.debug("REST request to get movement by id: {}", id);
        ResultResponse<MovementResponse, String> response = movementService.findById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieves all movements for a specific account.
     * Optionally filter by date range.
     * 
     * @param accountId The account ID (required)
     * @param startDate Optional start date for filtering
     * @param endDate Optional end date for filtering
     * @return ResponseEntity with list of movements
     */
    @GetMapping
    public ResponseEntity<ResultResponse<List<MovementResponse>, String>> getMovementsByAccount(
            @RequestParam UUID accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("REST request to get movements for account: {}, from: {}, to: {}", 
                accountId, startDate, endDate);
        ResultResponse<List<MovementResponse>, String> response = 
                movementService.findByAccountId(accountId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Calculates current balance for an account.
     * Includes daily limit information.
     * 
     * @param accountId The account ID
     * @return ResponseEntity with balance details
     */
    @GetMapping("/account/{accountId}/balance")
    public ResponseEntity<ResultResponse<BalanceResponse, String>> getAccountBalance(
            @PathVariable UUID accountId) {
        log.debug("REST request to get balance for account: {}", accountId);
        ResultResponse<BalanceResponse, String> response = movementService.calculateBalance(accountId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Calculates daily limit usage for an account.
     * Shows how much of the daily limit has been used today.
     * 
     * @param accountId The account ID
     * @return ResponseEntity with daily limit details
     */
    @GetMapping("/account/{accountId}/daily-limit")
    public ResponseEntity<ResultResponse<BalanceResponse, String>> getDailyLimitUsed(
            @PathVariable UUID accountId) {
        log.debug("REST request to get daily limit used for account: {}", accountId);
        ResultResponse<BalanceResponse, String> response = movementService.calculateDailyLimitUsed(accountId);
        return ResponseEntity.ok(response);
    }
}
