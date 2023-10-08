package com.example.orderapi.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestModel {
    private String cardNumber;
    private String cvv;
    private BigDecimal amount;
}