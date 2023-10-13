package com.example.orderapi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String guestEmail;
    private String couponCode;
    private Integer couponID;
    private BigDecimal amount;
    private Date createdAt;
    private List<OrderItemDTO> orderItems;
}
