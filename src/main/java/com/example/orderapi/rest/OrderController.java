package com.example.orderapi.rest;

import com.example.orderapi.model.TransactionRequestModel;
import com.example.orderapi.repository.entity.Order;
import com.example.orderapi.repository.entity.Product;
import com.example.orderapi.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    WebClient couponApiClient;

    @Autowired
    WebClient storeApiClient;

    @Autowired
    WebClient bankApiClient;

    @Autowired
    WebClient notificationApiClient;

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestParam(required = false) String coupon_code,
                                             @RequestBody List<Product> products) {

        // validate and consume coupon from coupon-api
        if (coupon_code != null) {
            ResponseEntity<Void> couponResponse = couponApiClient.post()
                    .uri("/coupons/consume/{code}", coupon_code)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            if (couponResponse.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.badRequest().build();
            }
        }

        // consume stock from store-api
        ResponseEntity<Void> stockResponse = storeApiClient.post()
                .uri("/products/consume-stock")
                .bodyValue(products)
                .retrieve()
                .toBodilessEntity()
                .block();

        if (stockResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(stockResponse.getStatusCode()).build();
        }

        // withdraw amount from customer via bank-api and save transaction ID
        BigDecimal invoiceAmount = products.stream().map(Product::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        TransactionRequestModel transactionRequestModel = new TransactionRequestModel();
        transactionRequestModel.setAmount(invoiceAmount);
        // TODO: set field values of transaction request model, i.e., cardNumber and cvv fields.

        ResponseEntity<Integer> withdrawalResponse = bankApiClient.post()
                .uri("/transaction/withdraw")
                .bodyValue(transactionRequestModel)
                .retrieve()
                .toEntity(Integer.class)
                .block();

        if (withdrawalResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(withdrawalResponse.getStatusCode()).build();
        }

        // deposit amount to merchant via bank-api
        ResponseEntity<Void> depositResponse = bankApiClient.post()
                .uri("/transaction/deposit")
                .bodyValue(transactionRequestModel)
                .retrieve()
                .toBodilessEntity()
                .block();

        if (depositResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(depositResponse.getStatusCode()).build();
        }

        // send order notifications
        ResponseEntity<Void> notificationResponse = notificationApiClient.post()
                .uri("/notifications/send")
                .bodyValue("notification-request")
                .retrieve()
                .toBodilessEntity()
                .block();

        if (notificationResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(notificationResponse.getStatusCode()).build();
        }

        // store the order
        Order order = new Order();
        order.setProducts(products);
        order.setCreatedAt(LocalDateTime.now());
        order.setTransactionId(withdrawalResponse.getBody());
        order.setAmount(invoiceAmount);
        // TODO: set user, couponId, and transactionId of order
        orderService.createOrder(order);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{customerEmail}")
    public ResponseEntity<List<Order>> ordersMadeByCustomer(@PathVariable String customerEmail) {
        List<Order> orders = orderService.findOrdersByCustomerEmail(customerEmail);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(orders);
        }
    }

//    @GetMapping
//    public ResponseEntity<List<Order>> getOrdersByDateRange(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//
//        List<Order> orders = orderService.findOrdersByCreatedAtBetween(startDate, endDate);
//        if (orders.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.ok(orders);
//        }
//    }
}