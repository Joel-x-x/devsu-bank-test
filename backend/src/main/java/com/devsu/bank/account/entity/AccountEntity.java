package com.devsu.bank.account.entity;

import com.devsu.bank.auditable.Auditable;
import com.devsu.bank.customer.entity.CustomerEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
public class AccountEntity extends Auditable {
    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber; // Unique key

    @Enumerated(EnumType.STRING)
    private AccountTypeEnum accountType;

    @Column(name = "initial_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal initialBalance;

    @Column(name = "daily_limit", nullable = false, precision = 19, scale = 4)
    private BigDecimal dailyLimit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    private CustomerEntity customer;
}
