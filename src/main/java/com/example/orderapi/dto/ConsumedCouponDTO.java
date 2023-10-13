package com.example.orderapi.dto;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class ConsumedCouponDTO {
    private Integer id;
    private BigDecimal actualDiscount;
}