package com.example.orderapi.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class IdsException extends RuntimeException {
    private final Set<Long> ids;

    public IdsException(String message, Set<Long> ids) {
        super(message);
        this.ids = ids;
    }

}
