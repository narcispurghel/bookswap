package com.github.narcispurghel.bookswap.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Table(value = "email_verifications")
public class EmailVerification {

    @Id
    private UUID userId;

    private String verificationCode;

    private LocalDateTime expiresIn = LocalDateTime.now().plusSeconds(300);

    public EmailVerification() {

    }

    public EmailVerification(UUID userId, String verificationCode) {
        this.userId = userId;
        this.verificationCode = verificationCode;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public LocalDateTime getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(LocalDateTime expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof EmailVerification that)) {
            return false;
        }
        return Objects.equals(userId, that.userId) &&
                Objects.equals(verificationCode, that.verificationCode) &&
                Objects.equals(expiresIn, that.expiresIn);
    }

    @Override public int hashCode() {
        return Objects.hash(userId, verificationCode, expiresIn);
    }
}
