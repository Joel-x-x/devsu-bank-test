package com.devsu.bank.customer.service;

import com.devsu.bank.customer.dto.CustomerRequest;
import com.devsu.bank.customer.dto.CustomerResponse;
import com.devsu.bank.customer.dto.CustomerUpdateRequest;
import com.devsu.bank.customer.entity.CustomerEntity;
import com.devsu.bank.customer.mapper.CustomerMapper;
import com.devsu.bank.customer.repository.CustomerRepository;
import com.devsu.bank.customer.repository.CustomerSpecification;
import com.devsu.bank.infrastructure.exception.EntityAlreadyExistsException;
import com.devsu.bank.infrastructure.exception.EntityNotFoundException;
import com.devsu.bank.infrastructure.pagination.FilterRequest;
import com.devsu.bank.infrastructure.pagination.PageResponse;
import com.devsu.bank.infrastructure.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of CustomerService interface.
 * Handles all business logic related to Customer operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {
    
    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final PasswordEncoder passwordEncoder;
    
    // ========== CRUD OPERATIONS ==========
    
    @Override
    public ResultResponse<CustomerResponse, String> create(CustomerRequest request) {
        log.info("Creating customer with identification: {}", request.identification());
        
        // Validate unique identification
        if (repository.existsByIdentification(request.identification())) {
            throw new EntityAlreadyExistsException("Customer", "identification", request.identification());
        }
        
        CustomerEntity entity = mapper.toEntity(request);
        
        // Encrypt password
        entity.setPassword(passwordEncoder.encode(request.password()));
        
        // Set default status if not provided
        if (entity.getStatus() == null) {
            entity.setStatus(true);
        }
        
        CustomerEntity saved = repository.save(entity);
        CustomerResponse response = mapper.toResponse(saved);
        
        log.info("Customer created successfully with code: {}", saved.getCustomerCode());
        return ResultResponse.created(response, "Customer created successfully");
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<CustomerResponse, String> findById(UUID id) {
        log.debug("Finding customer by id: {}", id);
        
        CustomerResponse response = repository.findById(id)
            .filter(customer -> customer.getDeletedAt() == null)
            .map(mapper::toResponse)
            .orElseThrow(() -> new EntityNotFoundException("Customer", id));
        
        return ResultResponse.success(response);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<List<CustomerResponse>, String> findAll() {
        log.debug("Finding all customers");
        
        List<CustomerResponse> customers = repository.findAll().stream()
            .filter(customer -> customer.getDeletedAt() == null)
            .map(mapper::toResponse)
            .toList();
        
        return ResultResponse.success(customers);
    }
    
    @Override
    public ResultResponse<CustomerResponse, String> update(UUID id, CustomerUpdateRequest request) {
        log.info("Updating customer with id: {}", id);
        
        CustomerEntity entity = findEntityById(id);
        
        // Update fields
        mapper.updateEntityFromDto(request, entity);
        
        CustomerEntity updated = repository.save(entity);
        CustomerResponse response = mapper.toResponse(updated);
        
        log.info("Customer updated successfully: {}", id);
        return ResultResponse.updated(response);
    }
    
    @Override
    public ResultResponse<CustomerResponse, String> partialUpdate(UUID id, CustomerUpdateRequest request) {
        log.info("Partial update for customer with id: {}", id);
        
        // The mapper is already configured to ignore null values
        return update(id, request);
    }
    
    @Override
    public ResultResponse<Void, String> delete(UUID id) {
        log.info("Soft deleting customer with id: {}", id);
        
        CustomerEntity entity = findEntityById(id);
        
        entity.softDelete("SYSTEM");
        entity.setStatus(false);
        repository.save(entity);
        
        log.info("Customer soft deleted successfully: {}", id);
        return ResultResponse.deleted();
    }
    
    @Override
    public ResultResponse<Void, String> hardDelete(UUID id) {
        log.warn("Hard deleting customer with id: {}", id);
        
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Customer", id);
        }
        
        repository.deleteById(id);
        log.info("Customer hard deleted successfully: {}", id);
        return ResultResponse.deleted();
    }
    
    // ========== CUSTOMER-SPECIFIC OPERATIONS ==========
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<CustomerResponse, String> findByCustomerCode(String customerCode) {
        log.debug("Finding customer by code: {}", customerCode);
        
        CustomerResponse response = repository.findByCustomerCode(customerCode)
            .filter(customer -> customer.getDeletedAt() == null)
            .map(mapper::toResponse)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Customer not found with code: %s", customerCode)
            ));
        
        return ResultResponse.success(response);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<CustomerResponse, String> findByIdentification(String identification) {
        log.debug("Finding customer by identification: {}", identification);
        
        CustomerResponse response = repository.findByIdentification(identification)
            .filter(customer -> customer.getDeletedAt() == null)
            .map(mapper::toResponse)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Customer not found with identification: %s", identification)
            ));
        
        return ResultResponse.success(response);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<List<CustomerResponse>, String> findActiveCustomers() {
        log.debug("Finding active customers");
        
        List<CustomerResponse> customers = repository.findAll().stream()
            .filter(customer -> customer.getDeletedAt() == null)
            .filter(customer -> Boolean.TRUE.equals(customer.getStatus()))
            .map(mapper::toResponse)
            .toList();
        
        return ResultResponse.success(customers);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<List<CustomerResponse>, String> findInactiveCustomers() {
        log.debug("Finding inactive customers");
        
        List<CustomerResponse> customers = repository.findAll().stream()
            .filter(customer -> customer.getDeletedAt() == null)
            .filter(customer -> Boolean.FALSE.equals(customer.getStatus()))
            .map(mapper::toResponse)
            .toList();
        
        return ResultResponse.success(customers);
    }
    
    @Override
    public ResultResponse<CustomerResponse, String> activateCustomer(UUID id) {
        log.info("Activating customer: {}", id);
        
        CustomerEntity entity = findEntityById(id);
        entity.setStatus(true);
        
        CustomerEntity updated = repository.save(entity);
        CustomerResponse response = mapper.toResponse(updated);
        
        log.info("Customer activated successfully: {}", id);
        return ResultResponse.updated(response);
    }
    
    @Override
    public ResultResponse<CustomerResponse, String> deactivateCustomer(UUID id) {
        log.info("Deactivating customer: {}", id);
        
        CustomerEntity entity = findEntityById(id);
        entity.setStatus(false);
        
        CustomerEntity updated = repository.save(entity);
        CustomerResponse response = mapper.toResponse(updated);
        
        log.info("Customer deactivated successfully: {}", id);
        return ResultResponse.updated(response);
    }
    
    // ========== PAGINATION ==========
    
    @Override
    @Transactional(readOnly = true)
    public ResultResponse<PageResponse<CustomerResponse>, String> findAllPaginated(FilterRequest filterRequest) {
        log.debug("Finding customers with pagination - page: {}, size: {}, search: {}", 
                filterRequest.getPage(), filterRequest.getSize(), filterRequest.getSearchValue());
        
        // Create sort
        Sort sort = filterRequest.getSortDirection().equalsIgnoreCase("DESC")
                ? Sort.by(filterRequest.getSortBy()).descending()
                : Sort.by(filterRequest.getSortBy()).ascending();
        
        // Create pageable
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
        
        // Execute query with specification
        Page<CustomerEntity> page = repository.findAll(
                CustomerSpecification.withFilters(filterRequest.getSearchValue()),
                pageable
        );
        
        // Map to response
        List<CustomerResponse> content = page.getContent().stream()
                .map(mapper::toResponse)
                .toList();
        
        PageResponse<CustomerResponse> pageResponse = PageResponse.of(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
        
        return ResultResponse.success(pageResponse);
    }
    
    // ========== PRIVATE HELPER METHODS ==========
    
    private CustomerEntity findEntityById(UUID id) {
        return repository.findById(id)
            .filter(customer -> customer.getDeletedAt() == null)
            .orElseThrow(() -> new EntityNotFoundException("Customer", id));
    }
}
