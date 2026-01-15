package com.devsu.bank.customer.repository;

import com.devsu.bank.customer.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID>, JpaSpecificationExecutor<CustomerEntity> {
    Optional<CustomerEntity> findByCustomerCode(String customerCode);
    Optional<CustomerEntity> findByIdentification(String identification);
    boolean existsByCustomerCode(String customerCode);
    boolean existsByIdentification(String identification);
}
