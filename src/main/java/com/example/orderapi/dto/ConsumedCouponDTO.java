package com.example.orderapi.dto;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class ConsumedCouponDTO {
    private Long id;
    private BigDecimal actualDiscount;
}