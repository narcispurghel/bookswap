package com.github.narcispurghel.bookswap.service.impl;

import com.github.narcispurghel.bookswap.entity.Authority;
import com.github.narcispurghel.bookswap.entity.User;
import com.github.narcispurghel.bookswap.entity.UserAuthority;
import com.github.narcispurghel.bookswap.enums.AuthorityType;
import com.github.narcispurghel.bookswap.mapper.UserMapper;
import com.github.narcispurghel.bookswap.model.*;
import com.github.narcispurghel.bookswap.repository.AuthorityRepository;
import com.github.narcispurghel.bookswap.repository.UserAuthorityRepository;
import com.github.narcispurghel.bookswap.repository.UserRepository;
import com.github.narcispurghel.bookswap.service.UserService;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
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
        return userRepository.findByEmail(username);
    }
    
    @Override
    public Mono<UserWithAuthorities> getUserWithAuthoritiesByEmailAsync(String email) {
        return userRepository.findUserDtoByEmail(email)
                             .flatMap(userDto -> {
                                 Mono<Set<UUID>> authoritiesIds =
                                         userAuthorityRepository.findAllByUserId(userDto.id())
                                                                .map(UserAuthority::getAuthorityId)
                                                                .collect(Collectors.toSet());
                                 Mono<Set<AuthorityDto>> authoritiesDtoMono =
                                         authoritiesIds.flatMap(uuids -> authorityRepository.findAllAuthorityDtoByIds(
                                                 uuids).collect(Collectors.toSet()));
                                 return Mono.zip(Mono.just(userDto), authoritiesDtoMono)
                                            .map(tuple -> {
                                                UserDto fetchedUserDto = tuple.getT1();
                                                Set<AuthorityDto>
                                                        fetchedAuthorityDtos =
                                                        tuple.getT2();
                                                return UserMapper.toUserWithAuthorities(
                                                        fetchedUserDto,
                                                        fetchedAuthorityDtos);
                                            });
                             });
    }
    
    @Override
    @Transactional
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
        user.setPassword(requestBody.data().password());
        Mono<User> userMono = userRepository.save(user);
        return Mono.zip(authorityMono, userMono)
                   .flatMap(tuple -> {
                       Authority fetchedAuthority = tuple.getT1();
                       User fetchedUser = tuple.getT2();
                       UserAuthority userAuthority = new UserAuthority();
                       userAuthority.setUserId(user.getId());
                       userAuthority.setAuthorityId(fetchedAuthority.getId());
                       return userAuthorityRepository.save(userAuthority)
                                                     .flatMap(userAuthoritySaved -> getUserWithAuthoritiesByEmailAsync(
                                                             fetchedUser.getUsername()));
                   });
    }
}
