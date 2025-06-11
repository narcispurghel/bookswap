package com.github.narcispurghel.bookswap.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @Email
        String email,
        
        @NotBlank
        String firstName,
        
        @NotBlank
        String lastName,
        
        @NotBlank
        @Size(min = 8)
        String password
) {
}
