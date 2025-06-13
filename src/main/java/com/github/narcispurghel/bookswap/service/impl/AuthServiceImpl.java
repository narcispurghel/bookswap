package com.github.narcispurghel.bookswap.service.impl;

import com.github.narcispurghel.bookswap.entity.Authority;
import com.github.narcispurghel.bookswap.entity.User;
import com.github.narcispurghel.bookswap.entity.UserAuthority;
import com.github.narcispurghel.bookswap.enums.AuthorityType;
import com.github.narcispurghel.bookswap.enums.JwtTokenType;
import com.github.narcispurghel.bookswap.model.Data;
import com.github.narcispurghel.bookswap.model.LoginRequest;
import com.github.narcispurghel.bookswap.model.SignupRequest;
import com.github.narcispurghel.bookswap.model.UserWithAuthorities;
import com.github.narcispurghel.bookswap.repository.AuthorityRepository;
import com.github.narcispurghel.bookswap.repository.UserAuthorityRepository;
import com.github.narcispurghel.bookswap.repository.UserRepository;
import com.github.narcispurghel.bookswap.service.AuthService;
import com.github.narcispurghel.bookswap.service.JwtService;
import com.github.narcispurghel.bookswap.service.UserService;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger LOGGER = Loggers.getLogger(AuthServiceImpl.class);

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final JwtService jwtService;

    public AuthServiceImpl(ReactiveAuthenticationManager reactiveAuthenticationManager,
            PasswordEncoder passwordEncoder,
            UserService userService,
            UserRepository userRepository,
            AuthorityRepository authorityRepository,
            UserAuthorityRepository userAuthorityRepository,
            JwtService jwtService) {
        this.reactiveAuthenticationManager = reactiveAuthenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Map<String, String>> authenticate(LoginRequest loginRequest,
            ServerWebExchange serverWebExchange) {
        if (loginRequest == null) {
            return Mono.error(new ResponseStatusException(
                    HttpStatusCode.valueOf(400))); // TODO Create custom exceptions
        }
        return userService.existsByEmail(loginRequest.email())
                .filter(Boolean::valueOf)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatusCode.valueOf(401),
                                "Bad credentials"))) // TODO Create custom exceptions
                .doOnNext(exists -> LOGGER.info("Found user in database"))
                .flatMap(exist -> generateAuthenticationToken(loginRequest))
                .flatMap(authentication -> generateTokensAndCookies(authentication,
                        serverWebExchange));
    }

    @Transactional
    @Override
    public Mono<UserWithAuthorities> saveUserAsync(Data<SignupRequest> requestBody) {
        if (requestBody == null) {
            return Mono.error(new ResponseStatusException(HttpStatusCode.valueOf(400)));
        }
        Mono<Authority> authorityMono =
                authorityRepository.findByAuthorityType(AuthorityType.USER.name())
                        .switchIfEmpty(Mono.error(() -> new ResponseStatusException(
                                HttpStatusCode.valueOf(500),
                                "No authority found with name USER")));
        User user = new User();
        user.setEmail(requestBody.data().email());
        user.setFirstName(requestBody.data().firstName());
        user.setLastName(requestBody.data().lastName());
        user.setPassword(passwordEncoder.encode(requestBody.data().password()));
        Mono<User> userMono = userRepository.save(user);
        return Mono.zip(authorityMono, userMono)
                .flatMap(tuple -> {
                    Authority fetchedAuthority = tuple.getT1();
                    User fetchedUser = tuple.getT2();
                    UserAuthority userAuthority = new UserAuthority();
                    userAuthority.setUserId(user.getId());
                    userAuthority.setAuthorityId(fetchedAuthority.getId());
                    return userAuthorityRepository.save(userAuthority)
                            .flatMap(userAuthoritySaved ->
                                    userService.getUserWithAuthoritiesByEmailAsync(
                                            fetchedUser.getEmail()));
                });
    }

    @Override
    public Mono<String> logout(ServerWebExchange serverWebExchange) {
        return generateLogoutCookie().map(logoutCookies -> {
                    serverWebExchange.getResponse().addCookie(logoutCookies.get(
                            "accessCookie"));
                    serverWebExchange.getResponse().addCookie(logoutCookies.get(
                            "refreshCookie"));
                    return serverWebExchange;
                }).contextWrite(
                        context -> ReactiveSecurityContextHolder.withAuthentication(null))
                .map(exchange -> "Logout success!");
    }

    private Mono<Map<String, ResponseCookie>> generateLogoutCookie() {
        Map<String, ResponseCookie> logoutCookies = new HashMap<>();
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", null)
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(false) // TODO change to true in production
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", null)
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(false) // TODO change to true in production
                .build();
        logoutCookies.put("accessCookie", accessCookie);
        logoutCookies.put("refreshCookie", refreshCookie);
        return Mono.just(logoutCookies);
    }

    private Mono<Authentication> generateAuthenticationToken(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginRequest.email(),
                        loginRequest.password());
        return reactiveAuthenticationManager.authenticate(authToken)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatusCode.valueOf(401),
                                "Bad credentials"))) // TODO Create custom exceptions
                .onErrorMap(
                        error -> new ResponseStatusException(HttpStatusCode.valueOf(401),
                                "Bad credentials")); // TODO Create custom exceptions
    }

    private Mono<Map<String, String>> generateTokensAndCookies(
            Authentication authentication, ServerWebExchange serverWebExchange) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken",
                        jwtService.generateToken(userDetails, JwtTokenType.REFRESH))
                .path("/")
                .httpOnly(true)
                .secure(false) // TODO Set to true in production
                .maxAge(3600)
                .build();
        ResponseCookie accessCookie = ResponseCookie.from("accessToken",
                        jwtService.generateToken(userDetails, JwtTokenType.ACCESS))
                .path("/")
                .httpOnly(true)
                .secure(false) // TODO Set to true in production
                .maxAge(360)
                .build();
        serverWebExchange.getResponse().addCookie(refreshCookie);
        serverWebExchange.getResponse().addCookie(accessCookie);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("accessToken", accessCookie.getValue());
        responseBody.put("refreshToken", refreshCookie.getValue());
        return Mono.just(responseBody);
    }
}
