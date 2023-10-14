package com.example.orderapi.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderResponseItem {
    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private int quantity;
}
