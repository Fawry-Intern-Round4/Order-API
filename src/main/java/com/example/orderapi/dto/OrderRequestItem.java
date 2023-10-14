package com.example.orderapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class OrderRequestItem {
    @NotNull
    @Positive(message = "Product ID must be a positive integer.")
    private Long productId;

    @NotNull
    @Positive(message = "Store ID must be a positive integer.")
    private Long storeId;

    @Positive(message = "Quantity must be a positive integer.")
    private int quantity;
}
