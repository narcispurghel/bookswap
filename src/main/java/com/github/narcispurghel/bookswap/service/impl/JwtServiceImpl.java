package com.github.narcispurghel.bookswap.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.narcispurghel.bookswap.enums.JwtTokenType;
import com.github.narcispurghel.bookswap.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {
    private static final String AUDIENCE = "bookswap";
    private static final String ISSUER = "bookswap";

    private final String jwtSecretKey;

    public JwtServiceImpl(
            @Value(value = "${security.jwt.secret-key}") String jwtSecretKey) {
        this.jwtSecretKey = jwtSecretKey;
    }

    @Override
    public String generateToken(UserDetails userDetails, JwtTokenType jwtTokenType) {
        if (userDetails == null || jwtTokenType == null) {
            throw new IllegalArgumentException("userSecurity or jwtTokenType is null");
        }
        long jwtExpiration = (jwtTokenType == JwtTokenType.ACCESS ? 360 : 3600);
        return JWT.create()
                .withAudience(AUDIENCE)
                .withIssuer(ISSUER)
                .withIssuedAt(Instant.now())
                .withSubject(userDetails.getUsername())
                .withExpiresAt(Instant.now().plusSeconds(jwtExpiration))
                .withJWTId(UUID.randomUUID().toString())
                .sign(Algorithm.HMAC512(jwtSecretKey));
    }

    @Override
    public String generateToken(DecodedJWT refreshToken, JwtTokenType jwtTokenType) {
        if (refreshToken == null || jwtTokenType == null) {
            throw new IllegalArgumentException("refreshToken or jwtTokenType is null");
        }
        long jwtExpiration = (jwtTokenType == JwtTokenType.ACCESS ? 360 : 3600);
        return JWT.create()
                .withAudience(AUDIENCE)
                .withIssuer(ISSUER)
                .withIssuedAt(Instant.now())
                .withSubject(refreshToken.getSubject())
                .withExpiresAt(Instant.now().plusSeconds(jwtExpiration))
                .withJWTId(UUID.randomUUID().toString())
                .sign(Algorithm.HMAC512(jwtSecretKey));
    }

    @Override
    public DecodedJWT decodeToken(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC512(jwtSecretKey))
                .withAudience(AUDIENCE)
                .withIssuer(ISSUER)
                .acceptExpiresAt(5)
                .build();
        return jwtVerifier.verify(token);
    }
}
