package com.devsu.bank.infrastructure.service;

import com.devsu.bank.infrastructure.response.ResultResponse;

import java.util.List;
import java.util.UUID;

/**
 * Generic CRUD service interface that provides standard operations for entities.
 * All methods return ResultResponse for consistent API responses.
 * 
 * @param <REQUEST> The request DTO type for creating entities
 * @param <RESPONSE> The response DTO type for returning entities
 * @param <UPDATE_REQUEST> The request DTO type for updating entities
 */
public interface CrudService<REQUEST, RESPONSE, UPDATE_REQUEST> {
    
    /**
     * Creates a new entity.
     * 
     * @param request The request object containing entity data
     * @return ResultResponse containing the created entity
     */
    ResultResponse<RESPONSE, String> create(REQUEST request);
    
    /**
     * Finds an entity by its ID.
     * 
     * @param id The entity ID
     * @return ResultResponse containing the entity
     */
    ResultResponse<RESPONSE, String> findById(UUID id);
    
    /**
     * Retrieves all entities (excluding soft-deleted ones).
     * 
     * @return ResultResponse containing list of all entities
     */
    ResultResponse<List<RESPONSE>, String> findAll();
    
    /**
     * Updates an entity with all fields from the request.
     * 
     * @param id The entity ID
     * @param request The request object containing updated data
     * @return ResultResponse containing the updated entity
     */
    ResultResponse<RESPONSE, String> update(UUID id, UPDATE_REQUEST request);
    
    /**
     * Partially updates an entity (only non-null fields).
     * 
     * @param id The entity ID
     * @param request The request object containing updated data
     * @return ResultResponse containing the updated entity
     */
    ResultResponse<RESPONSE, String> partialUpdate(UUID id, UPDATE_REQUEST request);
    
    /**
     * Soft deletes an entity by setting deletedAt timestamp.
     * 
     * @param id The entity ID
     * @return ResultResponse confirming deletion
     */
    ResultResponse<Void, String> delete(UUID id);
    
    /**
     * Permanently deletes an entity from the database.
     * 
     * @param id The entity ID
     * @return ResultResponse confirming hard deletion
     */
    ResultResponse<Void, String> hardDelete(UUID id);
}
