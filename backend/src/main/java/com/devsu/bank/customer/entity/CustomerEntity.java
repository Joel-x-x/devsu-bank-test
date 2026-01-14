package com.devsu.bank.customer.entity;

import com.devsu.bank.person.entity.PersonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.security.SecureRandom;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer")
public class CustomerEntity extends PersonEntity {
    @Column(name = "password", nullable = false, length = 64)
    private String password;

    @Column(name = "status", nullable = false, columnDefinition = "BIT DEFAULT 1")
    @Builder.Default
    private Boolean status = true;

    @Column(name = "customer_code", nullable = false, unique = true, length = 10)
    private String customerCode;

    @PrePersist
    private void generateCustomerCode() {
        if(this.customerCode == null) {
            this.customerCode = String.format(
                    "%08d",
                    new SecureRandom().nextInt(100_000_000)
            );
        }
    }
}
