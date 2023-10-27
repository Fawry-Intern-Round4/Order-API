package com.example.orderapi.service;


import com.example.orderapi.dto.*;
import com.example.orderapi.error.GeneralError;
import com.example.orderapi.error.IdsError;
import com.example.orderapi.exception.ClientException;
import com.example.orderapi.exception.IdsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WebClientServiceImpl implements WebClientService {
    private final WebClient.Builder webClient;
    private final KafkaTemplate<String, OrderDTO> kafkaTemplate;

    @Override
    public List<ProductResponse> getProducts(Set<Long> productIds) {
        return webClient.build()
                .get()
                .uri("http://product-api:8080/product", uriBuilder ->
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
        webClient.build()
                .get()
                .uri("http://coupon-api:8080/coupon/validation", uriBuilder -> uriBuilder
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
        return Objects.requireNonNull(webClient.build()
                        .get()
                        .uri("http://coupon-api:8080/coupon/discount", uriBuilder -> uriBuilder
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
        webClient.build()
                .post()
                .uri("http://store-api:8080/stock/validation")
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
        bankPayment(withdrawRequestModel, false);
    }

    @Override
    public void depositInvoiceAmountIntoMerchantBankAccount(TransactionRequestModel depositRequestModel) {
        bankPayment(depositRequestModel, true);
    }

    private void bankPayment(TransactionRequestModel transactionRequestModel, boolean isDeposit) {
        webClient.build()
                .post()
                .uri("https://bank-api-service-iiyh.onrender.com/transaction/" + (isDeposit ? "deposit" : "withdraw"))
                .bodyValue(transactionRequestModel)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse
                                .bodyToMono(LinkedHashMap.class)
                                .flatMap(linkedHashMap -> {
                                            LinkedHashMap<?, ?> error =
                                                    (LinkedHashMap<?, ?>) linkedHashMap.get("error");
                                            ClientException clientException = new ClientException(
                                                    (int) error.get("status")
                                                    , (String) error.get("message")
                                                    , (String) error.get("timestamp"));
                                            return Mono.error(clientException);
                                        }
                                )
                )
                .toEntity(Void.class)
                .block();
    }


    @Override
    public void consumeStock(List<ItemRequest> itemRequests) {
        webClient.build()
                .put()
                .uri("http://store-api:8080/stock/consumption")
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
        webClient.build()
                .post()
                .uri("http://coupon-api:8080/coupon/consumption")
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
