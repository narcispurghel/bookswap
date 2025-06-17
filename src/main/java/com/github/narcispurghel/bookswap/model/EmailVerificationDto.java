package com.github.narcispurghel.bookswap.model;

import java.time.LocalDateTime;

public record EmailVerificationDto(
        String verificationCode,
        LocalDateTime expiresIn
) {
}
