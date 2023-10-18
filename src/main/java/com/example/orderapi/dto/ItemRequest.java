package com.example.orderapi.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
@Data
@Validated
public class ItemRequest {
    @NotNull(message = "Product ID is mandatory")
    private Long productId;
    @NotNull(message = "Store ID is mandatory")
    private Long storeId;
    @NotNull(message = "Quantity is mandatory")
    @Digits(integer = 10, fraction = 0, message = "Quantity must be a number")
    @PositiveOrZero(message = "Quantity must be positive")
    private int quantity;
}
