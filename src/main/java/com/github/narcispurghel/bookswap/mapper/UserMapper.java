package com.github.narcispurghel.bookswap.mapper;

import com.github.narcispurghel.bookswap.entity.User;
import com.github.narcispurghel.bookswap.model.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public final class UserMapper {
    private final PasswordEncoder passwordEncoder;

    private UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public static UserWithAuthorities toUserWithAuthorities(UserDto userDto,
            Set<AuthorityDto> authorityDto) {
        if (userDto == null || authorityDto == null) {
            throw new IllegalArgumentException("userDto or authorityDto is null");
        }
        return new UserWithAuthorities(userDto.id(), userDto.email(), userDto.firstName(),
                userDto.lastName(), userDto.createdAt(), userDto.updatedAt(),
                new HashSet<>(authorityDto));
    }

    public static VerificationDetails toVerificationDetails(UserWithAuthorities userDto,
            EmailVerificationDto emailDto) {
        if (userDto == null || emailDto == null) {
            throw new IllegalArgumentException("userDto or emailDto is null");
        }
        return new VerificationDetails(emailDto.verificationCode(), userDto.email(),
                userDto.firstName());
    }

    public User toUser(SignupRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        return user;
    }
}
