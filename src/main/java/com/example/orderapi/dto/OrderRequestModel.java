package com.example.orderapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class OrderRequestModel {
    String couponCode;
    @NotBlank(message = "Guest email is mandatory")
    @Email(message = "Guest email must be a valid email")
    String guestEmail;
    private TransactionRequestModel transactionRequestModel;
    private List<ItemRequest> orderRequestItems;

    public Set<Long> getProductIds() {
        return new HashSet<>(orderRequestItems.stream().map(ItemRequest::getProductId).toList());
    }
}
