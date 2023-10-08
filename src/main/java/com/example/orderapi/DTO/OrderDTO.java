package com.example.orderapi.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderDTO {
    private Integer id;
    private String guestEmail;
    private Integer couponID;
    private BigDecimal amount;
    private Date createdAt;
}
