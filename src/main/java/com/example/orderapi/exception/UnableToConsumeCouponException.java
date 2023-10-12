package com.example.orderapi.exception;

public class UnableToConsumeCouponException extends RuntimeException{
    public UnableToConsumeCouponException (String message){
        super(message);
    }
}
