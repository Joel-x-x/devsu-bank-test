package com.devsu.bank.account.repository;

import com.devsu.bank.account.AccountTypeEnum;
import com.devsu.bank.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID>, JpaSpecificationExecutor<AccountEntity> {
    Optional<AccountEntity> findByAccountNumber(String accountNumber);
    List<AccountEntity> findByCustomerId(UUID customerId);
    boolean existsByAccountNumber(String accountNumber);
    long countByCustomerIdAndAccountType(UUID customerId, AccountTypeEnum accountType);
}
