package com.github.narcispurghel.bookswap.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String password,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
