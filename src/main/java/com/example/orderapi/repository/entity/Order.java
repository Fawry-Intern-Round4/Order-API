package com.example.orderapi.repository.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Table (name = "orders")
@Entity (name = "order")
@Data
public class Order {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "coupon_id")
    private int couponId;

    @Column
    private BigDecimal amount;

    @ManyToMany
    @JoinTable(name = "orderProducts", joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
    @JsonManagedReference
    private List<Product> products;

    @Column(name = "transaction_id")
    private int transactionId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
