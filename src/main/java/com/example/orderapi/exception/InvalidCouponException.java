package com.example.orderapi.exception;

public class InvalidCouponException extends RuntimeException{
    public InvalidCouponException(String message){
        super(message);
    }
}
