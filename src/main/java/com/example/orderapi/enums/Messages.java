package com.example.orderapi.enums;

import lombok.Getter;

@Getter
public enum Messages {
    STOCK_NOT_AVAILABLE("Stock not available"),
    INVALID_COUPON("Invalid coupon"),
    PRODUCT_NOT_FOUND("Product Not Found"),
    UNABLE_TO_CONSUME_COUPON("Unable to consume coupon"),
    FAILED_WITHDRAW_PAYMENT_TRANSACTION("Failed withdraw payment transaction"),
    FAILED_DEPOSIT_PAYMENT_TRANSACTION("Failed deposit payment transaction");

    final String message;
    Messages(String message) {
        this.message = message;
    }
}
