package com.github.narcispurghel.bookswap.controller;

import com.github.narcispurghel.bookswap.model.Data;
import com.github.narcispurghel.bookswap.model.LoginRequest;
import com.github.narcispurghel.bookswap.model.SignupRequest;
import com.github.narcispurghel.bookswap.model.UserWithAuthorities;
import com.github.narcispurghel.bookswap.service.AuthService;
import jakarta.validation.Valid;
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
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(SIGNUP_ENDPOINT)
    public Mono<ResponseEntity<Data<UserWithAuthorities>>> signup(
            @RequestBody @Valid Data<SignupRequest> requestBody) {
        return authService.saveUserAsync(requestBody)
                .map(userWithAuthorities -> ResponseEntity.ok(Data.body(
                        userWithAuthorities)));
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
