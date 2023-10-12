package com.example.orderapi.exception;

public class FailedPaymentTransactionException extends RuntimeException{
    public FailedPaymentTransactionException(String message){
        super(message);
    }
}
