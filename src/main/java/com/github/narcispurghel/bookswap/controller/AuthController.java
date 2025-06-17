package com.github.narcispurghel.bookswap.controller;

import com.github.narcispurghel.bookswap.model.*;
import com.github.narcispurghel.bookswap.service.AuthService;
import com.github.narcispurghel.bookswap.service.EmailVerificationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.github.narcispurghel.bookswap.constant.EndpointsConstants.*;

@RestController
@RequestMapping(value = AUTHENTICATION_ENDPOINT,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    public AuthController(AuthService authService,
            EmailVerificationService emailVerificationService) {
        this.authService = authService;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping(SIGNUP_ENDPOINT)
    public Mono<ResponseEntity<Data<UserWithAuthorities>>> signup(
            @RequestBody @Valid Data<SignupRequest> requestBody) {
        LOGGER.debug("RequestBody is {}", requestBody);
        return authService.signup(requestBody.data())
                .doOnNext(data -> LOGGER.debug("Creating response"))
                .map(data -> ResponseEntity.status(201).body(Data.body(data)))
                .doOnError(ex -> LOGGER.debug("Exception occurred { }", ex));
    }

    @PostMapping(LOGIN_ENDPOINT)
    public Mono<Map<String, String>> login(@RequestBody LoginRequest requestBody,
            ServerWebExchange serverWebExchange) {
        return authService.authenticate(requestBody, serverWebExchange);
    }

    @PostMapping(LOGOUT_ENDPOINT)
    public Mono<ResponseEntity<String>> logout(ServerWebExchange serverWebExchange) {
        return authService.logout(serverWebExchange)
                .map(message -> ResponseEntity.ok(message));
    }
}
