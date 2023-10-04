package com.example.orderapi.service;

import com.example.orderapi.repository.entity.Order;

import java.util.List;

public interface OrderService {
    void createOrder(Order order);
    List<Order> findOrdersByCustomerEmail(String customerEmail);

//    List<Order> findOrdersByCreatedAtBetween(LocalDate startDate, LocalDate endDate);
}
