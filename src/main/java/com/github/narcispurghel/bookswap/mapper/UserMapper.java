package com.github.narcispurghel.bookswap.mapper;

import com.github.narcispurghel.bookswap.model.AuthorityDto;
import com.github.narcispurghel.bookswap.model.UserDto;
import com.github.narcispurghel.bookswap.model.UserWithAuthorities;

import java.util.HashSet;
import java.util.Set;

public final class UserMapper {

    private UserMapper() {

    }

    public static UserWithAuthorities toUserWithAuthorities(UserDto userDto,
            Set<AuthorityDto> authorityDto) {
        if (userDto == null || authorityDto == null) {
            throw new IllegalArgumentException("userDto or authorityDto is null");
        }
        return new UserWithAuthorities(userDto.id(), userDto.email(),
                userDto.firstName(), userDto.lastName(),
                userDto.password(), userDto.createdAt(),
                userDto.updatedAt(), new HashSet<>(authorityDto));
    }
}
