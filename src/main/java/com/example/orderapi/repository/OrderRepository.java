package com.example.orderapi.repository;

import com.example.orderapi.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<List<Order>> findOrdersByGuestEmail(String guestEmail);
    Optional<List<Order>> findOrdersByCreatedAtBetween(Date from, Date to);
}
