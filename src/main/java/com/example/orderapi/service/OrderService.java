package com.example.orderapi.service;

import com.example.orderapi.dto.OrderDTO;
import com.example.orderapi.dto.OrderRequestModel;

import java.util.Date;
import java.util.List;

public interface OrderService {
    OrderDTO createOrder(OrderRequestModel orderRequestModel);
    List<OrderDTO> findOrdersByGuestEmail(String guestEmail);
    List<OrderDTO> findOrdersByCreatedAtBetween(Date from, Date to);
}
