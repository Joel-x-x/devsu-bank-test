package com.devsu.bank.movement.mapper;

import com.devsu.bank.movement.dto.MovementRequest;
import com.devsu.bank.movement.dto.MovementResponse;
import com.devsu.bank.movement.entity.MovementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovementMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "availableBalance", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    MovementEntity toEntity(MovementRequest request);
    
    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "account.accountNumber", target = "accountNumber")
    MovementResponse toResponse(MovementEntity entity);
}
