package com.github.narcispurghel.bookswap.service.impl;

import com.github.narcispurghel.bookswap.repository.UserRepository;
import com.github.narcispurghel.bookswap.service.UserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override public Mono<UserDetails> findByUsername(String username) {
        //return userRepository.findByEmail(username);
        return Mono.just(User.withUsername("test")
                             .build());
    }
}
