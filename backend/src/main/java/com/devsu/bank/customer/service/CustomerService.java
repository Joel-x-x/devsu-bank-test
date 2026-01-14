package com.devsu.bank.customer.service;

import com.devsu.bank.customer.dto.CustomerRequest;
import com.devsu.bank.customer.dto.CustomerResponse;
import com.devsu.bank.customer.dto.CustomerUpdateRequest;
import com.devsu.bank.infrastructure.response.ResultResponse;
import com.devsu.bank.infrastructure.service.CrudService;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Customer operations.
 * Extends the generic CRUD operations and adds customer-specific methods.
 * All methods return ResultResponse for consistent API responses.
 */
public interface CustomerService extends CrudService<CustomerRequest, CustomerResponse, CustomerUpdateRequest> {
    
    /**
     * Finds a customer by their unique customer code.
     * 
     * @param customerCode The customer code
     * @return ResultResponse containing the customer data
     */
    ResultResponse<CustomerResponse, String> findByCustomerCode(String customerCode);
    
    /**
     * Finds a customer by their identification number.
     * 
     * @param identification The identification number
     * @return ResultResponse containing the customer data
     */
    ResultResponse<CustomerResponse, String> findByIdentification(String identification);
    
    /**
     * Retrieves all active customers.
     * 
     * @return ResultResponse containing list of active customers
     */
    ResultResponse<List<CustomerResponse>, String> findActiveCustomers();
    
    /**
     * Retrieves all inactive customers.
     * 
     * @return ResultResponse containing list of inactive customers
     */
    ResultResponse<List<CustomerResponse>, String> findInactiveCustomers();
    
    /**
     * Activates a customer account.
     * 
     * @param id The customer ID
     * @return ResultResponse containing the updated customer
     */
    ResultResponse<CustomerResponse, String> activateCustomer(UUID id);
    
    /**
     * Deactivates a customer account.
     * 
     * @param id The customer ID
     * @return ResultResponse containing the updated customer
     */
    ResultResponse<CustomerResponse, String> deactivateCustomer(UUID id);
}
