package com.github.narcispurghel.bookswap.model;

import jakarta.validation.Valid;

public record Data<T>(@Valid T data) {
    
    public static <U> Data<U> body(U body) {
        return new Data<>(body);
    }
}
