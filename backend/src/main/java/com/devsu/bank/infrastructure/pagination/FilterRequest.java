package com.devsu.bank.infrastructure.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic filter request for pagination with dynamic filters.
 * Supports flexible filtering by any field name and value.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequest {
    
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "id";
    private String sortDirection = "ASC"; // ASC or DESC
    
    // Generic filters - field name and value
    private String searchField;  // Field to search (e.g., "name", "identification")
    private String searchValue;  // Value to search for
    
    public int getPage() {
        return page != null && page >= 0 ? page : 0;
    }
    
    public int getSize() {
        return size != null && size > 0 && size <= 100 ? size : 10;
    }
}
