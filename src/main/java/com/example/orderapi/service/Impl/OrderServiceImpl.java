package com.example.orderapi.service.Impl;

import com.example.orderapi.model.TransactionRequestModel;
import com.example.orderapi.repository.OrderRepository;
import com.example.orderapi.repository.entity.Coupon;
import com.example.orderapi.repository.entity.Order;
import com.example.orderapi.repository.entity.Product;
import com.example.orderapi.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    WebClient couponApiClient;

    @Autowired
    WebClient storeApiClient;

    @Autowired
    WebClient bankApiClient;

    @Autowired
    WebClient notificationApiClient;

    @Override
    public void createOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public List<Order> findOrdersByGuestEmail(String guestEmail) {
        return orderRepository.findOrdersByGuestEmail(guestEmail).orElseGet(null);
    }

    @Override
    public ResponseEntity<Coupon> validateAndConsumeCouponCode(String couponCode){
        return couponApiClient.post()
                .uri("/coupons/consume?code={code}", couponCode)
                .retrieve()
                .toEntity(Coupon.class)
                .block();
    }

    @Override
    public ResponseEntity<Void> consumeProductStock(List<Product> products){
        return storeApiClient.post()
                .uri("/products/consume-stock")
                .bodyValue(products)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public ResponseEntity<Void> withdrawInvoiceAmountFromGuestBankAccount(TransactionRequestModel withdrawRequestModel) {
        return bankApiClient.post()
                .uri("/transaction/withdraw")
                .bodyValue(withdrawRequestModel)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public ResponseEntity<Void> depositInvoiceAmountIntoMerchantBankAccount(TransactionRequestModel depositRequestModel){
        return bankApiClient.post()
                .uri("/transaction/deposit")
                .bodyValue(depositRequestModel)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public ResponseEntity<Void> sendOrderDetailsToNotificationsAPI(Order order) {
        return notificationApiClient.post()
                .uri("/notifications/send")
                .bodyValue(order)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    //    @Override
//    public List<Order> findOrdersByCreatedAtBetween(LocalDate startDate, LocalDate endDate) {
//        LocalDateTime startDateTime = startDate.atStartOfDay();
//        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
//
//        return orderRepository.findOrdersByCreatedAtBetween(startDateTime, endDateTime).orElseGet(null);
//    }
}
