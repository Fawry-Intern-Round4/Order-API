package com.example.orderapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class CouponRequestDTO {

    private String code;

    private String customerEmail;

    private BigDecimal orderPrice;

    private Long orderId;
}
