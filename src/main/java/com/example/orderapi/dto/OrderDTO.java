package com.example.orderapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String guestEmail;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String couponCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long couponID;
    private BigDecimal amount;
    private Date createdAt;
    private List<OrderItemDTO> orderItems;
}
