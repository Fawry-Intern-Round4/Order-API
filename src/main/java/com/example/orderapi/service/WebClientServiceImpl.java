package com.example.orderapi.service;


import com.example.orderapi.dto.*;
import com.example.orderapi.exception.ClientException;
import com.example.orderapi.error.GeneralError;
import com.example.orderapi.error.IdsError;
import com.example.orderapi.exception.IdsException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class WebClientServiceImpl implements WebClientService {
    private final WebClient.Builder webClient;
    private final WebClient.Builder loadBalancedWebClient;

    private final KafkaTemplate<String, OrderDTO> kafkaTemplate;


    public WebClientServiceImpl(@Qualifier("webClientBuilder") WebClient.Builder webClient,
                                @Qualifier("loadBalancedWebClientBuilder") WebClient.Builder loadBalancedWebClient, KafkaTemplate<String, OrderDTO> kafkaTemplate) {
        this.webClient = webClient;
        this.loadBalancedWebClient = loadBalancedWebClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public List<ProductResponse> getProducts(Set<Long> productIds) {
        return loadBalancedWebClient.build()
                .get()
                .uri("lb://product-api/product", uriBuilder ->
                        uriBuilder.queryParam("ids", productIds).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse
                                .bodyToMono(IdsError.class)
                                .flatMap(idsError ->
                                        Mono.error(new IdsException(idsError.getMessage(), idsError.getIds()))
                                )
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse
                                .bodyToMono(GeneralError.class)
                                .flatMap(generalError ->
                                        Mono.error(new ClientException(
                                                generalError.getStatus(), generalError.getMessage(), generalError.getTimestamp()
                                        ))
                                )
                )
                .bodyToFlux(ProductResponse.class)
                .collectList()
                .block();
    }

    @Override
    public void validateCoupon(String couponCode, String customerEmail) {
        loadBalancedWebClient.build()
                .get()
                .uri("lb://coupon-api/coupon/validation", uriBuilder -> uriBuilder
                        .queryParam("code", couponCode)
                        .queryParam("customerEmail", customerEmail)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse
                                .bodyToMono(GeneralError.class)
                                .flatMap(generalError ->
                                        Mono.error(new ClientException(
                                                generalError.getStatus(), generalError.getMessage(), generalError.getTimestamp()
                                        ))
                                )
                )
                .bodyToMono(String.class)
                .block();
    }

    @Override
    public BigDecimal calculateDiscount(String couponCode, String customerEmail, BigDecimal invoiceAmount) {
        return Objects.requireNonNull(loadBalancedWebClient.build()
                        .get()
                        .uri("lb://coupon-api/coupon/discount", uriBuilder -> uriBuilder
                                .queryParam("code", couponCode)
                                .queryParam("customerEmail", customerEmail)
                                .queryParam("orderPrice", invoiceAmount)
                                .build())
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, clientResponse ->
                                clientResponse
                                        .bodyToMono(GeneralError.class)
                                        .flatMap(generalError ->
                                                Mono.error(new ClientException(
                                                        generalError.getStatus(), generalError.getMessage(), generalError.getTimestamp()
                                                ))
                                        )
                        )
                        .bodyToMono(DiscountDTO.class)
                        .block())
                .getActualDiscount();
    }

    @Override
    public void checkIfProductsOutOfStock(List<ItemRequest> itemRequests) {
        loadBalancedWebClient.build()
                .post()
                .uri("lb://store-api/stock/validation")
                .bodyValue(itemRequests)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse
                                .bodyToMono(IdsError.class)
                                .flatMap(idsError ->
                                        Mono.error(new IdsException(idsError.getMessage(), idsError.getIds()))
                                )

                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse
                                .bodyToMono(GeneralError.class)
                                .flatMap(generalError ->
                                        Mono.error(new ClientException(
                                                generalError.getStatus(), generalError.getMessage(), generalError.getTimestamp()
                                        ))
                                )
                )
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public void withdrawInvoiceAmountFromGuestBankAccount(TransactionRequestModel withdrawRequestModel) {
        webClient.build()
                .post()
                .uri("http://localhost:8081/transaction/withdraw")
                .bodyValue(withdrawRequestModel)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse
                                .bodyToMono(GeneralError.class)
                                .flatMap(generalError ->
                                        Mono.error(new ClientException(
                                                generalError.getStatus(), generalError.getMessage(), generalError.getTimestamp()
                                        ))
                                )
                )
                .toEntity(Void.class)
                .block();
    }

    @Override
    public void depositInvoiceAmountIntoMerchantBankAccount(TransactionRequestModel depositRequestModel) {
        webClient.build()
                .post()
                .uri("http://localhost:8081/transaction/deposit")
                .bodyValue(depositRequestModel)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse
                                .bodyToMono(GeneralError.class)
                                .flatMap(generalError ->
                                        Mono.error(new ClientException(
                                                generalError.getStatus(), generalError.getMessage(), generalError.getTimestamp()
                                        ))
                                )
                )
                .toEntity(Void.class)
                .block();
    }

    @Override
    public void consumeStock(List<ItemRequest> itemRequests) {
        loadBalancedWebClient.build()
                .put()
                .uri("lb://store-api/stock/consumption")
                .bodyValue(itemRequests)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse
                                .bodyToMono(IdsError.class)
                                .flatMap(idsError ->
                                        Mono.error(new IdsException(idsError.getMessage(), idsError.getIds()))
                                )

                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse
                                .bodyToMono(GeneralError.class)
                                .flatMap(generalError ->
                                        Mono.error(new ClientException(
                                                generalError.getStatus(), generalError.getMessage(), generalError.getTimestamp()
                                        ))
                                )
                )
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public void consumeCoupon(CouponRequestDTO couponRequestDTO) {
        loadBalancedWebClient.build()
                .post()
                .uri("lb://coupon-api/coupon/consumption")
                .bodyValue(couponRequestDTO)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse
                                .bodyToMono(GeneralError.class)
                                .flatMap(generalError ->
                                        Mono.error(new ClientException(
                                                generalError.getStatus(), generalError.getMessage(), generalError.getTimestamp()
                                        ))
                                )
                )
                .bodyToMono(String.class)
                .block();
    }

    @Override
    public void sendOrderDetailsToNotificationsAPI(OrderDTO orderDTO) {
        kafkaTemplate.send("notification_topic", orderDTO);
    }

}
