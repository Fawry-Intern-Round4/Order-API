package com.example.orderapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ExceptionsHandlerController {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        CustomError error = new CustomError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(error.toString());
    }

    @ExceptionHandler(InvalidCouponException.class)
    public ResponseEntity<String> handleInvalidCouponException(Exception ex) {
        CustomError error = new CustomError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).contentType(MediaType.APPLICATION_JSON).body(error.toString());
    }

    @ExceptionHandler({StockNotAvailableException.class, ProductNotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(Exception ex) {
        CustomError error = new CustomError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(error.toString());
    }

    @ExceptionHandler(FailedPaymentTransactionException.class)
    public ResponseEntity<String> handleFailedPaymentTransactionException(Exception ex) {
        CustomError error = new CustomError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(error.toString());
    }

    private record CustomError(String message) {
        @Override
        public String toString() {
            return "{\"message\" : \"" + this.message + "\"}";
        }
    }
}
