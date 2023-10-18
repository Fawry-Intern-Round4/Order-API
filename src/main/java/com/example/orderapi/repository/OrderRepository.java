package com.example.orderapi.repository;

import com.example.orderapi.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findOrdersByGuestEmail(String guestEmail);
    List<Order> findOrdersByCreatedAtBetween(Date from, Date to);
}
