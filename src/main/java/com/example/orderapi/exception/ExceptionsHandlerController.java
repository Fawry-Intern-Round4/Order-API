package com.example.orderapi.exception;

import com.example.orderapi.error.GeneralError;
import com.example.orderapi.error.IdsError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;


@ControllerAdvice
public class ExceptionsHandlerController {

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<GeneralError> handleClientException(ClientException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(GeneralError.generateGeneralError(ex.getStatus(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody GeneralError handleGeneralException(Exception ex) {
        return GeneralError.generateGeneralError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }
    @ExceptionHandler(InvalidCouponException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public @ResponseBody GeneralError handleInvalidCouponException(Exception ex) {
        return GeneralError.generateGeneralError(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage());
    }

    @ExceptionHandler({StockNotAvailableException.class, ProductNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody GeneralError handleNotFoundException(Exception ex) {
        return GeneralError.generateGeneralError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(FailedPaymentTransactionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody GeneralError handleFailedPaymentTransactionException(Exception ex) {
        return GeneralError.generateGeneralError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody GeneralError handleConstraintViolationException(ConstraintViolationException e) {
        String message = ((ConstraintViolation<?>) e.getConstraintViolations().toArray()[0]).getMessage();
        return GeneralError.generateGeneralError(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody GeneralError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return GeneralError.generateGeneralError(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(IdsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody IdsError handleNotFoundProducts(IdsException e) {
        return IdsError.generateIdsError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e.getIds());
    }
}
