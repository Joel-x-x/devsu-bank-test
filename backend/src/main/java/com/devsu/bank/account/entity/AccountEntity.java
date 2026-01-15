package com.devsu.bank.account.entity;

import com.devsu.bank.account.AccountTypeEnum;
import com.devsu.bank.auditable.Auditable;
import com.devsu.bank.customer.entity.CustomerEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Random;

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

    @Column(name = "status", nullable = false, columnDefinition = "BIT DEFAULT 1")
    @Builder.Default
    private Boolean status = true;

    /**
     * Generates a unique 10-digit account number before persisting.
     */
    @PrePersist
    public void generateAccountNumber() {
        if (this.accountNumber == null || this.accountNumber.isEmpty()) {
            Random random = new Random();
            // Generate 10-digit account number
            long number = 1000000000L + (long)(random.nextDouble() * 9000000000L);
            this.accountNumber = String.valueOf(number);
        }
    }
}
