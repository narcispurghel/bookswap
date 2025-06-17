package com.github.narcispurghel.bookswap.repository;

import com.github.narcispurghel.bookswap.entity.EmailVerification;
import com.github.narcispurghel.bookswap.model.EmailVerificationDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface EmailVerificationRepository
        extends ReactiveCrudRepository<EmailVerification, UUID> {

    @Query("""
            INSERT INTO email_verifications (user_id, verification_code, expires_in)
            VALUES (:userId, :verificationCode, :expiresIn)
            RETURNING *
            """)
    Mono<EmailVerificationDto> saveEmailVerification(@Param("userID") UUID userId,
            @Param("verificationCode") String verificationCode,
            @Param("expiresIn") LocalDateTime expiresIn);

    @Query("""
            SELECT ev.verification_code, ev.expiresIn
            FROM email_verifications ev
            WHERE ev.user_id = :userId
            """)
    Mono<EmailVerificationDto> findVerificationCodeByUserId(@Param("userId") UUID userId);
}
