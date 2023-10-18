package com.example.orderapi.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientException extends RuntimeException {
    private final int status;
    private final String timestamp;
    public ClientException(int status, String message, String timestamp) {
        super(message);
        this.status = status;
        this.timestamp = timestamp;
    }
}
