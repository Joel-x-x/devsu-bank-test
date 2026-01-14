package com.devsu.bank.customer.dto;

import com.devsu.bank.person.GenreEnum;

public record CustomerUpdateRequest(
        String name,
        GenreEnum genre,
        String birthDate,
        String address,
        String phone,
        String password,
        Boolean status
) {
}
