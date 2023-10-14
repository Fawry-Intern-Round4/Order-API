package com.example.orderapi.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private Long productID;
    private BigDecimal price;
    private int quantity;
}
