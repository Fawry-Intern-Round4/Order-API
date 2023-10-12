package com.example.orderapi.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long id;
    private Long orderID;
    private Long productID;
    private BigDecimal price;
    private int quantity;
}
