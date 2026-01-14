package com.devsu.bank.movement.entity;

import com.devsu.bank.account.entity.AccountEntity;
import com.devsu.bank.auditable.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movement")
public class MovementEntity extends Auditable {
    @Column(name = "movement_date", nullable = false, columnDefinition = "DATETIME(6)")
    private LocalDateTime movementDate;

    @Enumerated(EnumType.STRING)
    private MovementTypeEnum movementType;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(name = "available_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal availableBalance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private AccountEntity account;
}



