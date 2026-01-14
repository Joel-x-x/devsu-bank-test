package com.devsu.bank.customer.mapper;

import com.devsu.bank.customer.dto.CustomerRequest;
import com.devsu.bank.customer.dto.CustomerResponse;
import com.devsu.bank.customer.dto.CustomerUpdateRequest;
import com.devsu.bank.customer.entity.CustomerEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerCode", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    CustomerEntity toEntity(CustomerRequest request);
    
    CustomerResponse toResponse(CustomerEntity entity);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerCode", ignore = true)
    @Mapping(target = "identification", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(CustomerUpdateRequest request, @MappingTarget CustomerEntity entity);
}
