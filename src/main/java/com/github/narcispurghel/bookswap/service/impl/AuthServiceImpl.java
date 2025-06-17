package com.github.narcispurghel.bookswap.service.impl;

import com.github.narcispurghel.bookswap.entity.Authority;
import com.github.narcispurghel.bookswap.entity.EmailVerification;
import com.github.narcispurghel.bookswap.entity.User;
import com.github.narcispurghel.bookswap.entity.UserAuthority;
import com.github.narcispurghel.bookswap.enums.AuthorityType;
import com.github.narcispurghel.bookswap.enums.JwtTokenType;
import com.github.narcispurghel.bookswap.mapper.UserMapper;
import com.github.narcispurghel.bookswap.model.*;
import com.github.narcispurghel.bookswap.repository.AuthorityRepository;
import com.github.narcispurghel.bookswap.repository.EmailVerificationRepository;
import com.github.narcispurghel.bookswap.repository.UserAuthorityRepository;
import com.github.narcispurghel.bookswap.repository.UserRepository;
import com.github.narcispurghel.bookswap.service.AuthService;
import com.github.narcispurghel.bookswap.service.EmailVerificationService;
import com.github.narcispurghel.bookswap.service.JwtService;
import com.github.narcispurghel.bookswap.service.UserService;
import io.swagger.v3.core.converter.ModelConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserMapper userMapper;
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;
    private final EmailVerificationRepository emailVerificationRepository;

    public AuthServiceImpl(UserMapper userMapper,
            ReactiveAuthenticationManager reactiveAuthenticationManager,
            PasswordEncoder passwordEncoder,
            UserService userService,
            UserRepository userRepository,
            AuthorityRepository authorityRepository,
            UserAuthorityRepository userAuthorityRepository,
            JwtService jwtService,
            EmailVerificationService emailVerificationService,
            EmailVerificationRepository emailVerificationRepository) {
        this.userMapper = userMapper;
        this.reactiveAuthenticationManager = reactiveAuthenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.jwtService = jwtService;
        this.emailVerificationService = emailVerificationService;
        this.emailVerificationRepository = emailVerificationRepository;
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
                .doOnError(error -> LOGGER.debug("User not found in database"))
                .doOnNext(exists -> LOGGER.info("Found user in database"))
                .flatMap(exist -> generateAuthenticationToken(loginRequest))
                .flatMap(authentication -> generateTokensAndCookies(authentication,
                        serverWebExchange));
    }

    @Override
    public Mono<UserWithAuthorities> signup(SignupRequest request) {
        if (request == null) {
            return Mono.error(new ResponseStatusException(HttpStatusCode.valueOf(400)));
        }
        return this.saveUser(request)
                .doOnSuccess(user -> {
                    emailVerificationRepository.findById(user.id())
/*                            .doOnSuccess(verification -> {
                                emailVerificationService.sendCode(new VerificationDetails(
                                                verification.getVerificationCode(),
                                                user.email(),
                                                user.firstName()))
                                        .subscribe();
                            })*/
                            .subscribe();
                });
    }

    @Transactional
    private Mono<UserWithAuthorities> saveUser(SignupRequest request) {
        if (request == null) {
            return Mono.error(new ResponseStatusException(HttpStatusCode.valueOf(400)));
        }
        Mono<Authority> authorityMono =
                authorityRepository.findByAuthorityType(AuthorityType.USER.name())
                        .switchIfEmpty(Mono.error(() -> new ResponseStatusException(
                                HttpStatusCode.valueOf(500),
                                "No authority found with name USER")));
        Mono<User> userMono = userRepository.save(userMapper.toUser(request));
        return Mono.zip(authorityMono, userMono)
                .flatMap(tuple -> {
                    Authority fetchedAuthority = tuple.getT1();
                    User fetchedUser = tuple.getT2();
                    UserAuthority userAuthority = new UserAuthority();
                    userAuthority.setUserId(fetchedUser.getId());
                    userAuthority.setAuthorityId(fetchedAuthority.getId());
                    return emailVerificationService.generateCode()
                            .flatMap(
                                    code -> emailVerificationRepository.saveEmailVerification(
                                            fetchedUser.getId(), code,
                                            LocalDateTime.now().plusSeconds(360)))
                            // TODO simplify by returning
                            //  UserWithAuthority when saving
                            //  to db
                            .flatMap(
                                    verification -> userAuthorityRepository.save(
                                            userAuthority))
                            .flatMap(userAuthoritySaved ->
                                    userService.getUserWithAuthoritiesByEmail(
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
        LOGGER.info("Generating authentication token");
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginRequest.email(),
                        loginRequest.password());
        return reactiveAuthenticationManager.authenticate(authToken)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatusCode.valueOf(401),
                                "Bad credentials"))) // TODO Create custom exceptions
                .onErrorMap(ex -> {
                    LOGGER.debug("Error generating authentication token { }", ex);
                    return new ResponseStatusException(HttpStatusCode.valueOf(401),
                            ex.getMessage());
                }); // TODO Create custom exceptions
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
