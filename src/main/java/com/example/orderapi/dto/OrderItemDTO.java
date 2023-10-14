package com.example.orderapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private Long productID;
    private String productName;
    private String productPhoto;
    private Long orderID;
    private BigDecimal price;
    private int quantity;
}
