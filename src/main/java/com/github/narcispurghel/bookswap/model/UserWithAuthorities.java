package com.github.narcispurghel.bookswap.model;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserWithAuthorities(
        UUID id,
        String email,
        String firstName,
        String lastName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Set<AuthorityDto> authorities
) {
}
