package com.github.narcispurghel.bookswap.service;

import com.github.narcispurghel.bookswap.model.Data;
import com.github.narcispurghel.bookswap.model.LoginRequest;
import com.github.narcispurghel.bookswap.model.SignupRequest;
import com.github.narcispurghel.bookswap.model.UserWithAuthorities;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface AuthService {
    Mono<Map<String, String>> authenticate(LoginRequest data,
            ServerWebExchange serverWebExchange);

    Mono<UserWithAuthorities> saveUserAsync(Data<SignupRequest> requestBody);

    Mono<String> logout(ServerWebExchange serverWebExchange);
}
