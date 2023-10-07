package com.example.orderapi.rest;

import com.example.orderapi.model.TransactionRequestModel;
import com.example.orderapi.repository.entity.Coupon;
import com.example.orderapi.repository.entity.Order;
import com.example.orderapi.repository.entity.Product;
import com.example.orderapi.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderService orderService;


    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestParam(required = false) String couponCode,
                                             @RequestBody List<Product> products) {

        // validate and consume coupon from coupon-api
        ResponseEntity<Coupon> couponResponse = null;
        if (couponCode != null) {
            couponResponse = orderService.validateAndConsumeCouponCode(couponCode);
        }

        // consume stock from store-api
        ResponseEntity<Void> stockResponse = orderService.consumeProductStock(products);
        if (stockResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(stockResponse.getStatusCode()).build();
        }


        // withdraw invoice amount from customer's bank account
        BigDecimal invoiceAmount = products.stream().map(Product::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        Coupon coupon = null;
        if (couponResponse != null && couponResponse.getStatusCode() == HttpStatus.OK){
            coupon = couponResponse.getBody();
            if (coupon.getType().equals("fixed")) invoiceAmount = invoiceAmount.subtract(coupon.getValue());
            else invoiceAmount = invoiceAmount.subtract(invoiceAmount.multiply(coupon.getValue().divide(BigDecimal.valueOf(100))));
        }

        TransactionRequestModel withdrawRequestModel = new TransactionRequestModel();
        withdrawRequestModel.setAmount(invoiceAmount);
        // TODO: set field values of transaction request model, i.e., cardNumber and cvv.

        ResponseEntity<Void> withdrawalResponse = orderService.withdrawInvoiceAmountFromGuestBankAccount(withdrawRequestModel);
        if (withdrawalResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(withdrawalResponse.getStatusCode()).build();
        }

        // deposit invoice amount to merchant's bank account
        TransactionRequestModel depositRequestModel = new TransactionRequestModel();
        depositRequestModel.setAmount(invoiceAmount);
        // TODO: set field values of transaction request model, i.e., cardNumber and cvv.

        ResponseEntity<Void> depositResponse = orderService.depositInvoiceAmountIntoMerchantBankAccount(depositRequestModel);
        if (depositResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(depositResponse.getStatusCode()).build();
        }

        // create and store order
        Order order = new Order();
        order.setProducts(products);
        order.setAmount(invoiceAmount);
//        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        if (coupon != null) order.setCoupon(coupon);
        orderService.createOrder(order);

        // send order notifications
        ResponseEntity<Void> notificationResponse = orderService.sendOrderDetailsToNotificationsAPI(order);
        if (notificationResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(notificationResponse.getStatusCode()).build();
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{guestEmail:.+}")
    public ResponseEntity<List<Order>> ordersMadeByCustomer(@PathVariable String guestEmail) {
        List<Order> orders = orderService.findOrdersByGuestEmail(guestEmail);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(orders);
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