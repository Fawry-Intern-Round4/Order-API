package com.example.orderapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionRequestModel {
    @NotBlank(message = "Card number is mandatory")
    @Size(min = 16, max = 16, message = "Card number must be 16 digits")
    private String cardNumber;

    @NotBlank(message = "CVV is mandatory")
    @Size(min = 3, max = 3, message = "CVV must be 3 digits")
    private String cvv;

    private BigDecimal amount;
}
