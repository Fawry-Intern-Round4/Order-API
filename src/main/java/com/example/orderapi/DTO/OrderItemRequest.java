package com.example.orderapi.DTO;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long productId;
    private Long storeId;
    private int quantity;
}
