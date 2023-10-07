package com.example.orderapi.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Table (name = "orders")
@Entity (name = "order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "guest_email")
    private String guestEmail;

    @ManyToOne
    @JoinColumn(name="coupon_id")
    private Coupon coupon;

    @Column
    private BigDecimal amount;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "orderProducts", joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
    @JsonManagedReference
    private List<Product> products;
}
