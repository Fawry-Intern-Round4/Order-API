package com.example.orderapi.DTO;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
@Getter
public class OrderItemResponse {
    private Long productId;
    private Long storeId;
    private String productName;
    private BigDecimal price;
    private boolean available;
    private int quantity;
}
