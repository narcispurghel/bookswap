package com.github.narcispurghel.bookswap.controller;

import com.github.narcispurghel.bookswap.model.Data;
import com.github.narcispurghel.bookswap.model.SignupRequest;
import com.github.narcispurghel.bookswap.model.UserWithAuthorities;
import com.github.narcispurghel.bookswap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.github.narcispurghel.bookswap.constant.EndpointsConstants.AUTHENTICATION_ENDPOINT;
import static com.github.narcispurghel.bookswap.constant.EndpointsConstants.SIGNUP_ENDPOINT;

@RestController
@RequestMapping(value = AUTHENTICATION_ENDPOINT,
                produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping(SIGNUP_ENDPOINT)
    public Mono<ResponseEntity<Data<UserWithAuthorities>>> signup(
            @RequestBody @Valid Data<SignupRequest> requestBody) {
        return userService.saveUserAsync(requestBody)
                          .map(userWithAuthorities -> ResponseEntity.ok(Data.body(
                                  userWithAuthorities)));
    }
}
