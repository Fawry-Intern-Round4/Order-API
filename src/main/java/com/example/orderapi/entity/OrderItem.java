package com.example.orderapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "order_items")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Order ID is mandatory")
    private Long orderId;

    @NotNull(message = "Product ID is mandatory")
    private Long productId;

    @NotNull(message = "Product price is mandatory")
    @Digits(integer = 10, fraction = 2, message = "Product price must be a number")
    @Positive(message = "Product price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Quantity is mandatory")
    @Positive(message = "Quantity must be greater than 0")
    private int quantity;
}
