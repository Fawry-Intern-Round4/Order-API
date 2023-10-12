package com.example.orderapi.service;

import com.example.orderapi.dto.OrderDTO;
import com.example.orderapi.dto.OrderItemRequest;

import java.util.Date;
import java.util.List;

public interface OrderService {
    OrderDTO createOrder(String guestEmail, String couponCode, List<OrderItemRequest> orderItems);
    OrderDTO saveOrder(OrderDTO orderDTO);
    List<OrderDTO> findOrdersByGuestEmail(String guestEmail);
    List<OrderDTO> findOrdersByCreatedAtBetween(Date from, Date to);
}
