package com.example.orderapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class OrderDTO {
    private Long id;
    private String guestEmail;
    private String couponCode;
    private BigDecimal amount;
    private Date createdAt;
    private List<OrderItemDTO> orderItems;
}
