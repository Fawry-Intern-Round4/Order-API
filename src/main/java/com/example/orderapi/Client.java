package com.example.orderapi;

import com.example.orderapi.dto.*;
import com.example.orderapi.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class Client {
    @Autowired
    OrderMapper orderMapper;

    private final WebClient.Builder webClient;

    public Client(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder;
    }

    public ResponseEntity<Boolean> validateCouponCode(String couponCode){
        return webClient.build()
                .post()
                .uri("lb://coupon-api/coupon/validate", uriBuilder -> uriBuilder.queryParam("code", couponCode).build())
                .retrieve()
                .toEntity(Boolean.class)
                .block();
    }
    
    public ResponseEntity<ConsumedCouponDTO> consumeCoupon(OrderDTO orderDTO){
        return webClient.build()
                .post()
                .uri("lb://coupon-api/coupon/consume")
                .bodyValue(orderDTO)
                .retrieve()
                .toEntity(ConsumedCouponDTO.class)
                .block();
    }

    public ResponseEntity<Boolean> consumeProductStock(List<OrderItemRequest> orderItems){
        return webClient.build()
                .post()
                .uri("lb://store-api/store/consume-stock")
                .bodyValue(orderItems)
                .retrieve()
                .toEntity(Boolean.class)
                .block();
    }
    
    public ResponseEntity<Void> withdrawInvoiceAmountFromGuestBankAccount(TransactionRequestModel withdrawRequestModel) {
        return webClient.build()
                .post()
                .uri("lb://bank-api/transaction/withdraw")
                .bodyValue(withdrawRequestModel)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public ResponseEntity<Void> depositInvoiceAmountIntoMerchantBankAccount(TransactionRequestModel depositRequestModel){
        return webClient.build()
                .post()
                .uri("lb://bank-api/transaction/deposit")
                .bodyValue(depositRequestModel)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
    
    public ResponseEntity<Void> sendOrderDetailsToNotificationsAPI(OrderDTO orderDTO) {
        return webClient.build()
                .post()
                .uri("lb://notification-api/send")
                .bodyValue(orderDTO)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public List<OrderItemResponse> fetchProductInformation(List<Long> productIDs) {
        return webClient.build()
                .get()
                .uri("lb://product-api/product", uriBuilder -> uriBuilder.queryParam("ids", productIDs).build())
                .retrieve()
                .bodyToFlux(OrderItemResponse.class)
                .collectList()
                .block();
    }
}
