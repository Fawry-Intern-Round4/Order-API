package com.example.orderapi.dto;

import lombok.Data;

@Data
public class CustomResponse {
    private int status;
    private String message;
    private String timestamp;
}