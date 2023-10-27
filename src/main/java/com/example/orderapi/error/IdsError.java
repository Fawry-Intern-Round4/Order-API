package com.example.orderapi.error;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdsError {
    private int status;
    private String message;
    private Set<Long> ids;
    private String timestamp;

    public static IdsError generateIdsError(int status, String message, Set<Long> ids) {
        LocalDate localDate = LocalDate.now();
        return new IdsError(status, message, ids, localDate.toString());
    }
}

