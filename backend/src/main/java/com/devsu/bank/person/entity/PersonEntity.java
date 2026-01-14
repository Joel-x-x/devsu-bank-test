package com.devsu.bank.person.entity;

import com.devsu.bank.auditable.Auditable;
import com.devsu.bank.person.GenreEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@ToString
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="person")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class PersonEntity extends Auditable {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    private GenreEnum genre;

    @Column(name = "birth_date", nullable = false, length = 10)
    private String birthDate;

    @Column(name = "identification", nullable = false, length = 20, unique = true)
    private String identification;

    @Column(name = "address", nullable = false, length = 150)
    private String address;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;
}
