package com.github.narcispurghel.bookswap.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.narcispurghel.bookswap.enums.JwtTokenType;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(UserDetails userDetails, JwtTokenType jwtTokenType);

    String generateToken(DecodedJWT refreshToken, JwtTokenType jwtTokenType);

    DecodedJWT decodeToken(String token);
}
