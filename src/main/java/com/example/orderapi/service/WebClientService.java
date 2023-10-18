package com.example.orderapi.service;


import com.example.orderapi.dto.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface WebClientService {
    List<ProductResponse> getProducts(Set<Long> productIds);

    void validateCoupon(String couponCode, String customerEmail);

    BigDecimal calculateDiscount(String couponCode, String customerEmail, BigDecimal invoiceAmount);

    void checkIfProductsOutOfStock(List<ItemRequest> itemRequests);

    void withdrawInvoiceAmountFromGuestBankAccount(TransactionRequestModel withdrawRequestModel);

    void depositInvoiceAmountIntoMerchantBankAccount(TransactionRequestModel depositRequestModel);

    void consumeStock(List<ItemRequest> itemRequests);

    void consumeCoupon(CouponRequestDTO couponRequestDTO);

    void sendOrderDetailsToNotificationsAPI(OrderDTO orderDTO);
}
