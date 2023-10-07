package com.example.orderapi.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Table(name = "coupons")
@Entity(name = "coupon")
@Data
public class Coupon {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    String type;

    @Column
    BigDecimal value;
}
