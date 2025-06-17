package com.github.narcispurghel.bookswap.service;

import com.github.narcispurghel.bookswap.model.UserWithAuthorities;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

public interface UserService extends ReactiveUserDetailsService {
    Mono<UserWithAuthorities> getUserWithAuthoritiesByEmail(String email);

    Mono<Boolean> existsByEmail(String email);
}
