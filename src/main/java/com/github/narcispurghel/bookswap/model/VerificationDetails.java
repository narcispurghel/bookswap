package com.github.narcispurghel.bookswap.model;

public record VerificationDetails(
        String code,
        String email,
        String firstName) {
}
