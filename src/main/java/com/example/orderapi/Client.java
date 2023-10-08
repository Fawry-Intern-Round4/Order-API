package com.example.orderapi;

import com.example.orderapi.DTO.*;
import com.example.orderapi.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

public class Client {
    @Autowired
    OrderMapper orderMapper;

    private final WebClient couponApiClient;
    private final WebClient storeApiClient;
    private final WebClient bankApiClient;
    private final WebClient notificationApiClient;

    public Client(WebClient.Builder webClientBuilder) {
        this.couponApiClient = webClientBuilder.baseUrl("COUPON_API_URL").build();
        this.storeApiClient = webClientBuilder.baseUrl("STORE_API_URL").build();
        this.bankApiClient = webClientBuilder.baseUrl("BANK_API_URL").build();
        this.notificationApiClient = webClientBuilder.baseUrl("Notification_API_URL").build();
    }
    
    public ResponseEntity<Boolean> validateCouponCode(String couponCode){
        return couponApiClient.post()
                .uri("/coupons/validate?code={code}", couponCode)
                .retrieve()
                .toEntity(Boolean.class)
                .block();
    }
    
    public ResponseEntity<CouponDTO> consumeCoupon(String couponCode){
        return couponApiClient.post()
                .uri("/coupons/consume?code={code}", couponCode)
                .retrieve()
                .toEntity(CouponDTO.class)
                .block();
    }
    
    public Mono<ResponseEntity<List<OrderItemResponse>>> consumeProductStock(List<OrderItemRequest> orderItems) {
        return storeApiClient.post()
                .uri("/products/consume-stock")
                .bodyValue(orderItems)
                .retrieve()
                .toEntityList(new ParameterizedTypeReference<OrderItemResponse>() {})
                .onErrorResume(WebClientResponseException.class, ex -> ex.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(ex));
    }
    
    public ResponseEntity<Void> withdrawInvoiceAmountFromGuestBankAccount(TransactionRequestModel withdrawRequestModel) {
        return bankApiClient.post()
                .uri("/transaction/withdraw")
                .bodyValue(withdrawRequestModel)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public ResponseEntity<Void> depositInvoiceAmountIntoMerchantBankAccount(TransactionRequestModel depositRequestModel){
        return bankApiClient.post()
                .uri("/transaction/deposit")
                .bodyValue(depositRequestModel)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
    
    public ResponseEntity<Void> sendOrderDetailsToNotificationsAPI(OrderDTO orderDTO) {
        return notificationApiClient.post()
                .uri("/notifications/send")
                .bodyValue(orderDTO)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
