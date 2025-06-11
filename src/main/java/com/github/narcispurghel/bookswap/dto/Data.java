package com.github.narcispurghel.bookswap.dto;

public record Data<T>(T data) {
    
    public static <U> Data<U> body(U body) {
        return new Data<>(body);
    }
}
