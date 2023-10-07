package com.example.orderapi.repository;

import com.example.orderapi.repository.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<List<Order>> findOrdersByGuestEmail(String guestEmail);
}
