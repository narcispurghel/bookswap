package com.github.narcispurghel.bookswap.service.impl;

import com.github.narcispurghel.bookswap.entity.EmailVerification;
import com.github.narcispurghel.bookswap.model.UserWithAuthorities;
import com.github.narcispurghel.bookswap.model.VerificationDetails;
import com.github.narcispurghel.bookswap.repository.EmailVerificationRepository;
import com.github.narcispurghel.bookswap.service.EmailVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(EmailVerificationServiceImpl.class);

    private final EmailVerificationRepository emailVerificationRepository;
    private final MailSender mailSender;
    private final String sender;


    public EmailVerificationServiceImpl(
            EmailVerificationRepository emailVerificationRepository,
            MailSender mailSender,
            @Value("${security.mail.username}") String sender) {
        this.emailVerificationRepository = emailVerificationRepository;
        this.mailSender = mailSender;
        this.sender = sender;
    }

    @Override public Mono<String> generateCode() {
        return Mono.just(String.valueOf(Math.round(Math.random() * 1000000)));
    }

    @Override
    @Transactional
    public Mono<VerificationDetails> save(UserWithAuthorities user) {
        return generateCode()
                .map(code -> new EmailVerification(user.id(), code))
                .flatMap(
                        emailVerification -> emailVerificationRepository.saveEmailVerification(
                                emailVerification.getUserId(),
                                emailVerification.getVerificationCode(),
                                emailVerification.getExpiresIn()))
                .map(emailVerificationDto -> new VerificationDetails(
                        emailVerificationDto.verificationCode(),
                        user.email(), user.firstName()))
                .doOnError(ex -> LOGGER.error("ex { }", ex));
    }

    @Override
    public Mono<Boolean> validateCode(String code, UUID userId) {
        return emailVerificationRepository.findVerificationCodeByUserId(userId)
                .switchIfEmpty(
                        Mono.error(new RuntimeException("EmailVerification not found")))
                .map(emailVerificationDto -> {
                    if (code.equals(emailVerificationDto.verificationCode()) &&
                            emailVerificationDto.expiresIn()
                                    .isAfter(LocalDateTime.now())) {
                        return true;
                    }
                    // TODO create custom exception
                    throw new RuntimeException("Invalid verification code");
                });
    }

    @Override
    public Mono<Void> sendCode(VerificationDetails details) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(sender);
        msg.setSubject("Verification Code");
        // TODO change to details.email() in production
        msg.setTo("narcispurghel.dev@gmail.com");
        msg.setText("Dear " + details.firstName() +
                ", thank you for using our app. Your verification code is " +
                details.code() + "." +
                "Take care that the expiration time is 5 minutes from now.");
        return Mono.fromRunnable(() -> {
                        this.mailSender.send(msg);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(ex -> {
                    LOGGER.error("Failed to send the email to {}", details.email());
                    LOGGER.error("{ }", ex);
                    // TODO create send_emails_queue table
                })
                .onErrorReturn(Mono.empty())
                .then();
    }
}
