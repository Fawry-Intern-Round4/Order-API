package com.example.orderapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestModel {
    private TransactionRequestModel transactionRequestModel;
    private List<OrderRequestItem> orderRequestItems;
}
