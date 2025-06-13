package com.github.narcispurghel.bookswap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@Controller
@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public Mono<String> home() {
        return Mono.just("home");
    }

    @GetMapping("/test")
    public Mono<String> test() {
        return Mono.just("test ok");
    }
}
