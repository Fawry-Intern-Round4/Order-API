package com.example.orderapi.model;

import com.example.orderapi.repository.entity.Product;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderModel {
    private int id;
//    private User user;
    private int couponId;
    private BigDecimal amount;
    private List<Product> products;
    private int transactionId;
//    private Timestamp createdAt;
}
