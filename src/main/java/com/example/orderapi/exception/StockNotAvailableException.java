package com.example.orderapi.exception;

public class StockNotAvailableException extends RuntimeException{
    public StockNotAvailableException(String message){
        super(message);
    }
}
