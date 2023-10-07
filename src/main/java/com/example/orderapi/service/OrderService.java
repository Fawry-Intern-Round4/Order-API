package com.example.orderapi.service;

import com.example.orderapi.model.TransactionRequestModel;
import com.example.orderapi.repository.entity.Coupon;
import com.example.orderapi.repository.entity.Order;
import com.example.orderapi.repository.entity.Product;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {
    void createOrder(Order order);
    List<Order> findOrdersByGuestEmail(String guestEmail);
    ResponseEntity<Coupon> validateAndConsumeCouponCode(String couponCode);
    ResponseEntity<Void> consumeProductStock(List<Product> products);
    ResponseEntity<Void> withdrawInvoiceAmountFromGuestBankAccount(TransactionRequestModel withdrawRequestModel);
    ResponseEntity<Void> depositInvoiceAmountIntoMerchantBankAccount(TransactionRequestModel depositRequestModel);
    ResponseEntity<Void> sendOrderDetailsToNotificationsAPI(Order order);
}
