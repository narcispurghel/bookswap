package com.github.narcispurghel.bookswap.model;

import java.util.UUID;

public record UserSecurity(
        UUID id,
        String email,
        String password,
        boolean isEmailVerified,
        boolean isAccountNonExpired,
        boolean isAccountNonLocked,
        boolean isCredentialsNonExpired,
        boolean isEnabled
) {
}
