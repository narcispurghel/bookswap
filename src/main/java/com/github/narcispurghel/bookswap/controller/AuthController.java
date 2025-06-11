package com.github.narcispurghel.bookswap.controller;

import com.github.narcispurghel.bookswap.dto.Data;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.github.narcispurghel.bookswap.constant.EndpointsConstants.AUTHENTICATION_ENDPOINT;
import static com.github.narcispurghel.bookswap.constant.EndpointsConstants.SIGNUP_ENDPOINT;

@RestController
@RequestMapping(value = AUTHENTICATION_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {
    
    @PostMapping(SIGNUP_ENDPOINT)
    public Mono<ResponseEntity<Data<String>>> signup() {
        return Mono.just(ResponseEntity.ok(Data.body("signup")));
    }
}
