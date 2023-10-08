package com.example.orderapi.service;

import com.example.orderapi.DTO.OrderDTO;
import com.example.orderapi.DTO.OrderItemRequest;

import java.util.Date;
import java.util.List;

public interface OrderService {
    OrderDTO createOrder(String couponCode, List<OrderItemRequest> orderItems);
    void saveOrder(OrderDTO orderDTO);
    List<OrderDTO> findOrdersByGuestEmail(String guestEmail);
    List<OrderDTO> findOrdersByCreatedAtBetween(Date from, Date to);
}
