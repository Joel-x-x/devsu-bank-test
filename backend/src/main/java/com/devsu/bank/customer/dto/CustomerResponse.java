package com.devsu.bank.customer.dto;

import com.devsu.bank.person.GenreEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        GenreEnum genre,
        String birthDate,
        String identification,
        String address,
        String phone,
        String customerCode,
        Boolean status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
