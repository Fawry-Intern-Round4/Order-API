package com.example.orderapi.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class GeneralError {
    private int status;
    private String message;
    private String timestamp;

    public GeneralError(int status, String message, String timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public static GeneralError generateGeneralError(int status, String message) {
        LocalDate localDate = LocalDate.now();
        return new GeneralError(status, message, localDate.toString());
    }
}
