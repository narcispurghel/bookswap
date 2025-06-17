package com.github.narcispurghel.bookswap.model;

public record UserWithAuthoritiesAndVerificationCode(UserWithAuthorities user,
        EmailVerificationDto verification) {
}
