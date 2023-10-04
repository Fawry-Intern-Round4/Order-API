package com.example.orderapi.service.Impl;

import com.example.orderapi.repository.OrderRepository;
import com.example.orderapi.repository.entity.Order;
import com.example.orderapi.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;

//    @Autowired
//    OrderMapper orderMapper;

    @Override
    public void createOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public List<Order> findOrdersByCustomerEmail(String customerEmail) {
        return orderRepository.findOrdersByCustomerEmail(customerEmail).orElseGet(null);
    }

//    @Override
//    public List<Order> findOrdersByCreatedAtBetween(LocalDate startDate, LocalDate endDate) {
//        LocalDateTime startDateTime = startDate.atStartOfDay();
//        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
//
//        return orderRepository.findOrdersByCreatedAtBetween(startDateTime, endDateTime).orElseGet(null);
//    }
}
