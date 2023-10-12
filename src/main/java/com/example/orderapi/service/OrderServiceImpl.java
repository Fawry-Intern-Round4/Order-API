package com.example.orderapi.service;

import com.example.orderapi.Client;
import com.example.orderapi.dto.*;
import com.example.orderapi.entity.Order;
import com.example.orderapi.enums.Messages;
import com.example.orderapi.exception.*;
import com.example.orderapi.mapper.OrderMapper;
import com.example.orderapi.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderMapper orderMapper;

    @Value("${system_bank_number}")
    private String systemBankNumber;

    Client client;

    @Override
    public OrderDTO createOrder(String guestEmail, String couponCode, List<OrderItemRequest> orderRequestItems) {
        if (couponCode != null && client.validateCouponCode(couponCode).getBody().equals(false))
            throw new InvalidCouponException(Messages.INVALID_COUPON.getMessage());

        if (client.consumeProductStock(orderRequestItems).getBody().equals(false))
            throw new StockNotAvailableException(Messages.STOCK_NOT_AVAILABLE.getMessage());

        List<Long> productIds = orderRequestItems.stream().map(OrderItemRequest::getProductId).toList();
        List<OrderItemResponse> orderItemsResponse = client.fetchProductInformation(productIds);
        if (orderItemsResponse.isEmpty()) {
            throw new ProductNotFoundException(Messages.PRODUCT_NOT_FOUND.getMessage());
        }

        BigDecimal invoiceAmount = Mono.just(orderItemsResponse)
                .map(orderItems -> orderItems.stream()
                        .map(orderItem -> orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .defaultIfEmpty(BigDecimal.ZERO)
                .block();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setAmount(invoiceAmount);
        orderDTO.setCouponCode(couponCode);
        orderDTO.setGuestEmail(guestEmail);
        OrderDTO orderDTO1 = saveOrder(orderDTO);

        ResponseEntity<ConsumedCouponDTO> consumedCouponDTOResponseEntity= client.consumeCoupon(orderDTO1);
        if (consumedCouponDTOResponseEntity.getStatusCode() != HttpStatus.OK)
            throw new UnableToConsumeCouponException(Messages.UNABLE_TO_CONSUME_COUPON.getMessage());

        ConsumedCouponDTO consumedCouponDTO = consumedCouponDTOResponseEntity.getBody();
        orderDTO1.setAmount(orderDTO1.getAmount().subtract(consumedCouponDTO.getActualDiscount()));
        orderDTO1.setCouponID(consumedCouponDTO.getId());

        List<OrderItemDTO> orderItemsDTO = orderItemsResponse.stream()
                .map(response -> {
                    OrderItemDTO orderItemDTO = new OrderItemDTO();
                    orderItemDTO.setProductID(response.getProductId());
                    orderItemDTO.setQuantity(response.getQuantity());
                    orderItemDTO.setPrice(response.getPrice());
                    orderItemDTO.setOrderID(orderDTO1.getId());
                    return orderItemDTO;
                })
                .collect(Collectors.toList());
        orderDTO1.setOrderItems(orderItemsDTO);
        saveOrder(orderDTO1);

        // transactions for invoice value withdraw and depost
        TransactionRequestModel withdrawRequestModel = new TransactionRequestModel();
        withdrawRequestModel.setAmount(invoiceAmount);
        // TODO: set field values of transaction request dto, i.e., cardNumber and cvv.
        ResponseEntity<Void> withdrawTransactionResponse = client.withdrawInvoiceAmountFromGuestBankAccount(withdrawRequestModel);
        if (withdrawTransactionResponse.getStatusCode() != HttpStatus.OK)
            throw new FailedPaymentTransactionException(Messages.FAILED_WITHDRAW_PAYMENT_TRANSACTION.getMessage());

        TransactionRequestModel depositRequestModel = new TransactionRequestModel();
        depositRequestModel.setAmount(invoiceAmount);
        depositRequestModel.setCardNumber(systemBankNumber);
        ResponseEntity<Void> depositTransactionResponse = client.depositInvoiceAmountIntoMerchantBankAccount(depositRequestModel);
        if (depositTransactionResponse.getStatusCode() != HttpStatus.OK)
            throw new FailedPaymentTransactionException(Messages.FAILED_DEPOSIT_PAYMENT_TRANSACTION.getMessage());

        // send order notifications
        client.sendOrderDetailsToNotificationsAPI(orderDTO);
        return orderDTO;
    }

    @Override
    public List<OrderDTO> findOrdersByGuestEmail(String guestEmail) {
        List<Order> orders = orderRepository.findOrdersByGuestEmail(guestEmail).orElseGet(null);
        return orders.stream().map(order -> orderMapper.toDTO(order)).collect(Collectors.toList());
    }

    @Override
    public OrderDTO saveOrder(OrderDTO orderDTO) {
        Order savedOrder = orderRepository.save(orderMapper.toEntity(orderDTO));
        return orderMapper.toDTO(savedOrder);
    }

    @Override
    public List<OrderDTO> findOrdersByCreatedAtBetween(Date from, Date to) {
        List<Order> orders = orderRepository.findOrdersByCreatedAtBetween(from, to).orElseGet(null);
        return orders.stream().map(order -> orderMapper.toDTO(order)).collect(Collectors.toList());
    }
}
