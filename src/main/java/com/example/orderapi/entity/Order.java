package com.example.orderapi.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table (name = "orders")
@Entity (name = "order")
@Data
public class Order extends BaseEntity{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guest_email")
    private String guestEmail;

    @Column(name="coupon_id", nullable = true)
    private Long couponID;

    @Column
    private BigDecimal amount;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();
}
