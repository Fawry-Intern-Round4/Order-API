package com.example.orderapi.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Table (name = "orders")
@Entity (name = "order")
@Data
public class Order extends BaseEntity{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "guest_email")
    private String guestEmail;

    @Column(name="coupon_id", nullable = true)
    private Integer couponID;

    @Column
    private BigDecimal amount;
}
