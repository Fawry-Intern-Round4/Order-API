package com.example.orderapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestModel {
    @NotBlank(message = "Card number is mandatory")
    @Size(min = 16, max = 16, message = "Card number must be 16 digits")
    private String cardNumber;

    private String cvv;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}
