package com.github.narcispurghel.bookswap.service;

import com.github.narcispurghel.bookswap.model.EmailVerificationDto;
import com.github.narcispurghel.bookswap.model.UserWithAuthorities;
import com.github.narcispurghel.bookswap.model.VerificationDetails;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EmailVerificationService {
    Mono<String> generateCode();

    Mono<VerificationDetails> save(UserWithAuthorities user);

    Mono<Boolean> validateCode(String code, UUID userId);

    Mono<Void> sendCode(VerificationDetails verificationDetails);
}
