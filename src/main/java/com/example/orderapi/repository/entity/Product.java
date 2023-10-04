package com.example.orderapi.repository.entity;


import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Table(name = "products")
@Entity(name = "product")
@Data
public class Product {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    BigDecimal price;
}