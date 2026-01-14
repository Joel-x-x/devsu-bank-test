package com.devsu.bank.customer.controller;

import com.devsu.bank.customer.dto.CustomerRequest;
import com.devsu.bank.customer.dto.CustomerResponse;
import com.devsu.bank.customer.dto.CustomerUpdateRequest;
import com.devsu.bank.customer.service.CustomerService;
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
 * REST controller for Customer operations.
 * Provides endpoints for CRUD operations and customer-specific queries.
 */
@Slf4j
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerService customerService;
    
    /**
     * Creates a new customer.
     * 
     * @param request The customer creation request
     * @return ResponseEntity with the created customer and 201 status
     */
    @PostMapping
    public ResponseEntity<ResultResponse<CustomerResponse, String>> createCustomer(
            @Valid @RequestBody CustomerRequest request) {
        log.info("REST request to create customer with identification: {}", request.identification());
        ResultResponse<CustomerResponse, String> response = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Retrieves a customer by ID.
     * 
     * @param id The customer ID
     * @return ResponseEntity with the customer data
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse<CustomerResponse, String>> getCustomerById(
            @PathVariable UUID id) {
        log.debug("REST request to get customer by id: {}", id);
        ResultResponse<CustomerResponse, String> response = customerService.findById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieves all customers.
     * 
     * @return ResponseEntity with list of all customers
     */
    @GetMapping
    public ResponseEntity<ResultResponse<List<CustomerResponse>, String>> getAllCustomers() {
        log.debug("REST request to get all customers");
        ResultResponse<List<CustomerResponse>, String> response = customerService.findAll();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Updates a customer completely.
     * 
     * @param id The customer ID
     * @param request The update request with all fields
     * @return ResponseEntity with the updated customer
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResultResponse<CustomerResponse, String>> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerUpdateRequest request) {
        log.info("REST request to update customer: {}", id);
        ResultResponse<CustomerResponse, String> response = customerService.update(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Partially updates a customer (only provided fields).
     * 
     * @param id The customer ID
     * @param request The partial update request
     * @return ResponseEntity with the updated customer
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ResultResponse<CustomerResponse, String>> partialUpdateCustomer(
            @PathVariable UUID id,
            @RequestBody CustomerUpdateRequest request) {
        log.info("REST request to partially update customer: {}", id);
        ResultResponse<CustomerResponse, String> response = customerService.partialUpdate(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Soft deletes a customer.
     * 
     * @param id The customer ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResultResponse<Void, String>> deleteCustomer(@PathVariable UUID id) {
        log.info("REST request to delete customer: {}", id);
        ResultResponse<Void, String> response = customerService.delete(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Permanently deletes a customer from the database.
     * 
     * @param id The customer ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<ResultResponse<Void, String>> hardDeleteCustomer(@PathVariable UUID id) {
        log.warn("REST request to hard delete customer: {}", id);
        ResultResponse<Void, String> response = customerService.hardDelete(id);
        return ResponseEntity.ok(response);
    }
    
    // ========== CUSTOMER-SPECIFIC ENDPOINTS ==========
    
    /**
     * Retrieves a customer by their customer code.
     * 
     * @param customerCode The customer code
     * @return ResponseEntity with the customer data
     */
    @GetMapping("/code/{customerCode}")
    public ResponseEntity<ResultResponse<CustomerResponse, String>> getCustomerByCode(
            @PathVariable String customerCode) {
        log.debug("REST request to get customer by code: {}", customerCode);
        ResultResponse<CustomerResponse, String> response = customerService.findByCustomerCode(customerCode);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieves a customer by their identification number.
     * 
     * @param identification The identification number
     * @return ResponseEntity with the customer data
     */
    @GetMapping("/identification/{identification}")
    public ResponseEntity<ResultResponse<CustomerResponse, String>> getCustomerByIdentification(
            @PathVariable String identification) {
        log.debug("REST request to get customer by identification: {}", identification);
        ResultResponse<CustomerResponse, String> response = customerService.findByIdentification(identification);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieves all active customers.
     * 
     * @return ResponseEntity with list of active customers
     */
    @GetMapping("/active")
    public ResponseEntity<ResultResponse<List<CustomerResponse>, String>> getActiveCustomers() {
        log.debug("REST request to get active customers");
        ResultResponse<List<CustomerResponse>, String> response = customerService.findActiveCustomers();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieves all inactive customers.
     * 
     * @return ResponseEntity with list of inactive customers
     */
    @GetMapping("/inactive")
    public ResponseEntity<ResultResponse<List<CustomerResponse>, String>> getInactiveCustomers() {
        log.debug("REST request to get inactive customers");
        ResultResponse<List<CustomerResponse>, String> response = customerService.findInactiveCustomers();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Activates a customer account.
     * 
     * @param id The customer ID
     * @return ResponseEntity with the updated customer
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ResultResponse<CustomerResponse, String>> activateCustomer(
            @PathVariable UUID id) {
        log.info("REST request to activate customer: {}", id);
        ResultResponse<CustomerResponse, String> response = customerService.activateCustomer(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Deactivates a customer account.
     * 
     * @param id The customer ID
     * @return ResponseEntity with the updated customer
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ResultResponse<CustomerResponse, String>> deactivateCustomer(
            @PathVariable UUID id) {
        log.info("REST request to deactivate customer: {}", id);
        ResultResponse<CustomerResponse, String> response = customerService.deactivateCustomer(id);
        return ResponseEntity.ok(response);
    }
}
