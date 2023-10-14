package com.example.orderapi.service;

import com.example.orderapi.dto.OrderDTO;
import com.example.orderapi.dto.OrderRequestModel;

import java.util.Date;
import java.util.List;

public interface OrderService {
    OrderDTO createOrder(String guestEmail, String couponCode, OrderRequestModel orderRequestModel);
    OrderDTO saveOrder(OrderDTO orderDTO);
    List<OrderDTO> findOrdersByGuestEmail(String guestEmail);
    List<OrderDTO> findOrdersByCreatedAtBetween(Date from, Date to);
    List<OrderDTO> findAllOrders();
}
