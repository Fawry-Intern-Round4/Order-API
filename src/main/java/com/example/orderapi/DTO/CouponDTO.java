package com.example.orderapi.DTO;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class CouponDTO {
    private Integer id;
    private String code;
    private BigDecimal value;
    private String type;
}