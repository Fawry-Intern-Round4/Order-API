package com.example.orderapi.controller;

import com.example.orderapi.dto.OrderDTO;
import com.example.orderapi.dto.OrderRequestModel;
import com.example.orderapi.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@RequestParam String guestEmail,
                                @RequestParam(required = false) String couponCode,
                                @Valid @RequestBody OrderRequestModel orderRequestModel) {

        return orderService.createOrder(guestEmail, couponCode, orderRequestModel);
    }

    @GetMapping("/{guestEmail:.+}")
    public List<OrderDTO> ordersMadeByCustomer(@PathVariable String guestEmail) {
        return orderService.findOrdersByGuestEmail(guestEmail);
    }

    @GetMapping
    public List<OrderDTO> findOrdersByCreatedAtBetween(@RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
                                                @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {

        return orderService.findOrdersByCreatedAtBetween(from, to);
    }
}