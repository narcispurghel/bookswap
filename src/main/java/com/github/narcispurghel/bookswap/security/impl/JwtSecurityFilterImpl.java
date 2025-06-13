package com.github.narcispurghel.bookswap.security.impl;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.narcispurghel.bookswap.enums.JwtTokenType;
import com.github.narcispurghel.bookswap.security.JwtSecurityFilter;
import com.github.narcispurghel.bookswap.service.JwtService;
import com.github.narcispurghel.bookswap.service.UserService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;
import reactor.util.annotation.NonNull;

import java.util.List;

import static com.github.narcispurghel.bookswap.constant.AuthConstants.ACCESS_TOKEN_COOKIE_NAME;
import static com.github.narcispurghel.bookswap.constant.AuthConstants.REFRESH_TOKEN_COOKIE_NAME;
import static com.github.narcispurghel.bookswap.constant.EndpointsConstants.PUBLIC_ENDPOINTS;

@Component
public class JwtSecurityFilterImpl implements JwtSecurityFilter {
    private static final Logger LOGGER = Loggers.getLogger(JwtSecurityFilterImpl.class);
    private static final String ERROR_LOG = "Sending error response";

    private final JwtService jwtService;
    private final UserService userService;

    public JwtSecurityFilterImpl(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override public Mono<Void> filter(@NotNull ServerWebExchange exchange,
            @NonNull WebFilterChain chain) {
        LOGGER.info("Entering JwtSecurityFilter");
        String path = exchange.getRequest().getPath().value();
        LOGGER.info("ServerWebExchange path is " + path);
        if (PUBLIC_ENDPOINTS.stream().anyMatch(path::equals)) {
            LOGGER.info("ServerWebExchange path is public, passing the filter");
            return chain.filter(exchange);
        }
        final MultiValueMap<String, HttpCookie>
                cookies = exchange.getRequest().getCookies();
        LOGGER.debug("Checking for authentication cookies");
        List<HttpCookie> accessTokenCookies = cookies.get(ACCESS_TOKEN_COOKIE_NAME);
        List<HttpCookie> refreshCookies = cookies.get(REFRESH_TOKEN_COOKIE_NAME);
        String refreshToken = null;
        if (refreshCookies != null && refreshCookies.getFirst() != null) {
            refreshToken = refreshCookies.getFirst().getValue();
        }
        HttpCookie accessTokenCookie;
        String accessToken;
        if (accessTokenCookies != null && accessTokenCookies.getFirst() != null) {
            accessTokenCookie = accessTokenCookies.getFirst();
            accessToken = accessTokenCookie.getValue();
            LOGGER.debug("accessToken found");
        } else {
            LOGGER.debug("accessToken not found");
            LOGGER.debug("Checking for refreshToken");
            if (refreshToken == null) {
                LOGGER.debug("refreshToken not found");
                LOGGER.debug(ERROR_LOG);
                return Mono.error(new ResponseStatusException(
                        HttpStatusCode.valueOf(401),
                        "Missing refresh token")
                );
            }
            LOGGER.info("refreshToken found");
            try {
                LOGGER.debug("Trying to validate refreshToken");
                DecodedJWT decodedRefreshToken = jwtService.decodeToken(refreshToken);
                LOGGER.debug("refreshToken is valid");
                LOGGER.debug("Generating another accessToken");
                accessToken = jwtService.generateToken(
                        decodedRefreshToken,
                        JwtTokenType.ACCESS
                );
                exchange.getResponse()
                        .addCookie(ResponseCookie.from("accessToken", accessToken)
                                .path("/")
                                .maxAge(360)
                                .build());
                LOGGER.debug("Adding new accessToken to HttpServerResponse");
            } catch (JWTVerificationException refreshTokenJwtEx) {
                LOGGER.debug("refreshToken is not valid");
                LOGGER.debug(ERROR_LOG);
                return Mono.error(
                        new ResponseStatusException(
                                HttpStatusCode.valueOf(401),
                                "Invalid refresh token")
                );
            }
        }
        String username;
        try {
            username = jwtService.decodeToken(accessToken).getSubject();
        } catch (TokenExpiredException ex) {
            LOGGER.debug("accessToken is expired");
            LOGGER.debug("Checking for refreshToken");
            if (refreshToken == null) {
                LOGGER.debug("refreshToken not found");
                LOGGER.debug(ERROR_LOG);
                return Mono.error(new ResponseStatusException(
                        HttpStatusCode.valueOf(401),
                        "Missing refresh token")
                );
            }
            LOGGER.info("refreshToken found");
            try {
                LOGGER.debug("Validating refreshToken");
                DecodedJWT decodedRefreshToken = jwtService.decodeToken(refreshToken);
                LOGGER.debug("refreshToken is valid");
                LOGGER.debug("Generating new accessToken");
                accessToken =
                        jwtService.generateToken(decodedRefreshToken,
                                JwtTokenType.ACCESS);
                username = jwtService.decodeToken(accessToken).getSubject();
                LOGGER.debug("Generating accessToken success");
                LOGGER.debug("Adding accessToken to ServerHttpResponse");
                exchange.getResponse()
                        .addCookie(ResponseCookie.from("accessToken", accessToken)
                                .path("/")
                                .maxAge(360)
                                .build());
            } catch (JWTVerificationException refreshTokenJwtEx) {
                LOGGER.debug("refreshToken is not valid");
                LOGGER.debug(ERROR_LOG);
                return Mono.error(
                        new ResponseStatusException(
                                HttpStatusCode.valueOf(401),
                                "Invalid refresh token")
                );
            }

        } catch (JWTVerificationException ex) {
            LOGGER.debug("accessToken is not valid");
            LOGGER.debug(ERROR_LOG);
            return Mono.error(new ResponseStatusException(HttpStatusCode.valueOf(401),
                    "Invalid accessToken"));
        }
        return userService.findByUsername(username)
                .flatMap(userDetails -> {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null,
                                    userDetails.getAuthorities());
                    return chain.filter(exchange).contextWrite(
                            ReactiveSecurityContextHolder.withAuthentication(authToken));
                });
    }
}
