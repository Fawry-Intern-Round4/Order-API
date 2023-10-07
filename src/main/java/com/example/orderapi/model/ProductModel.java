package com.example.orderapi.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductModel {
    BigDecimal price;

    int requiredQuantity;
}
