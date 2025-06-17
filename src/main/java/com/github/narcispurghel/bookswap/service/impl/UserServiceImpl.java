package com.github.narcispurghel.bookswap.service.impl;

import com.github.narcispurghel.bookswap.entity.Authority;
import com.github.narcispurghel.bookswap.entity.UserAuthority;
import com.github.narcispurghel.bookswap.mapper.UserMapper;
import com.github.narcispurghel.bookswap.model.*;
import com.github.narcispurghel.bookswap.repository.AuthorityRepository;
import com.github.narcispurghel.bookswap.repository.UserAuthorityRepository;
import com.github.narcispurghel.bookswap.repository.UserRepository;
import com.github.narcispurghel.bookswap.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final UserAuthorityRepository userAuthorityRepository;

    public UserServiceImpl(UserRepository userRepository,
            AuthorityRepository authorityRepository,
            UserAuthorityRepository userAuthorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.userAuthorityRepository = userAuthorityRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findUserSecurityByEmail(username).flatMap(userSecurity -> {
            Mono<Set<Authority>> authoritiesMono =
                    userAuthorityRepository.findAllByUserId(userSecurity.id())
                            .map(UserAuthority::getAuthorityId)
                            .collect(Collectors.toSet())
                            .flatMap(uuids -> authorityRepository.findAllById(uuids)
                                    .collect(Collectors.toSet()));
            return Mono.zip(Mono.just(userSecurity), authoritiesMono)
                    .map(tuple -> {
                        UserSecurity fetchedUserSecurity = tuple.getT1();
                        Set<Authority> fetchedAuthorities = tuple.getT2();
                        if (!fetchedUserSecurity.isEmailVerified()) {
                            // TODO create custom exception
                            throw new RuntimeException("Email is not verified");
                        }
                        return new UserDetailsDto(fetchedUserSecurity.email(),
                                fetchedUserSecurity.password(),
                                fetchedUserSecurity.isEmailVerified(),
                                fetchedUserSecurity.isAccountNonExpired(),
                                fetchedUserSecurity.isAccountNonLocked(),
                                fetchedUserSecurity.isCredentialsNonExpired(),
                                fetchedUserSecurity.isEnabled(),
                                fetchedAuthorities);
                    });
        });
    }

    @Override
    public Mono<UserWithAuthorities> getUserWithAuthoritiesByEmail(String email) {
        return userRepository.findUserDtoByEmail(email)
                .flatMap(userDto -> {
                    Mono<Set<UUID>> authoritiesIds =
                            userAuthorityRepository.findAllByUserId(userDto.id())
                                    .map(UserAuthority::getAuthorityId)
                                    .collect(Collectors.toSet());
                    Mono<Set<AuthorityDto>> authoritiesDtoMono =
                            authoritiesIds.flatMap(
                                    uuids -> authorityRepository.findAllAuthorityDtoByIds(
                                                    uuids)
                                            .collect(Collectors.toSet()));
                    return Mono.zip(Mono.just(userDto), authoritiesDtoMono)
                            .map(tuple -> {
                                UserDto fetchedUserDto = tuple.getT1();
                                Set<AuthorityDto> fetchedAuthorityDtos = tuple.getT2();
                                return UserMapper.toUserWithAuthorities(fetchedUserDto,
                                        fetchedAuthorityDtos);
                            });
                });
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
