package com.devsu.bank.customer.dto;

import com.devsu.bank.person.GenreEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerRequest(
        @NotBlank(message = "Name is required")
        String name,
        
        @NotNull(message = "Genre is required")
        GenreEnum genre,
        
        @NotBlank(message = "Birth date is required")
        String birthDate,
        
        @NotBlank(message = "Identification is required")
        String identification,
        
        @NotBlank(message = "Address is required")
        String address,
        
        @NotBlank(message = "Phone is required")
        String phone,
        
        @NotBlank(message = "Password is required")
        String password,
        
        Boolean status
) {
}
