package com.github.narcispurghel.bookswap.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import static com.github.narcispurghel.bookswap.constant.EndpointsConstants.DOCUMENTATION_ENDPOINT;
import static com.github.narcispurghel.bookswap.constant.EndpointsConstants.SCALAR_ENDPOINT;

@Controller
@RequestMapping(value = DOCUMENTATION_ENDPOINT, produces = MediaType.TEXT_HTML_VALUE)
public class DocsController {

    @GetMapping(SCALAR_ENDPOINT)
    public Mono<String> getScalar() {
        return Mono.just("scalar");
    }
}
