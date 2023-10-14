package com.example.orderapi.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Table(name = "order_items")
@Entity(name = "OrderItem")
@Data
public class OrderItem {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="order_id")
    private Long orderID;

    @Column(name = "product_id")
    private Long productID;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_photo")
    private String productPhoto;

    private BigDecimal price;
    private int quantity;
}
