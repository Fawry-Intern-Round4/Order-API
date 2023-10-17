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
import org.springframework.web.reactive.function.client.WebClient;
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

    public OrderServiceImpl(WebClient.Builder webClientBuilder) {
        this.client = new Client(webClientBuilder);
    }

    @Override
    public OrderDTO createOrder(String guestEmail, String couponCode, OrderRequestModel orderRequestModel) {
        List<OrderRequestItem> orderRequestItems = orderRequestModel.getOrderRequestItems();

        if (couponCode != null && !couponCode.isEmpty() && client.validateCouponCode(couponCode).getStatusCode() != HttpStatus.OK)
            throw new InvalidCouponException(Messages.INVALID_COUPON.getMessage());

        if (client.consumeProductStock(orderRequestItems).getBody().equals(false))
            throw new StockNotAvailableException(Messages.STOCK_NOT_AVAILABLE.getMessage());

        List<Long> productIds = orderRequestItems.stream().map(OrderRequestItem::getProductId).toList();
        List<OrderResponseItem> orderItemsResponse = client.fetchProductInformation(productIds);
        if (orderItemsResponse.isEmpty()) {
            throw new ProductNotFoundException(Messages.PRODUCT_NOT_FOUND.getMessage());
        }

        orderItemsResponse.forEach(response -> {
            orderRequestItems.stream()
                    .filter(req -> req.getProductId().equals(response.getId()))
                    .findFirst().ifPresent(request -> response.setQuantity(request.getQuantity()));
        });

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
        orderDTO1.setCouponCode(couponCode);

        if (couponCode != null && !couponCode.isEmpty()) {
            ResponseEntity<ConsumedCouponDTO> consumedCouponDTOResponseEntity= client.consumeCoupon(orderDTO1);
            if (consumedCouponDTOResponseEntity.getStatusCode() != HttpStatus.OK)
                throw new UnableToConsumeCouponException(Messages.UNABLE_TO_CONSUME_COUPON.getMessage());

            ConsumedCouponDTO consumedCouponDTO = consumedCouponDTOResponseEntity.getBody();
            orderDTO1.setAmount(orderDTO1.getAmount().subtract(consumedCouponDTO.getActualDiscount()));
            orderDTO1.setCouponID(consumedCouponDTO.getId());
        }

        List<OrderItemDTO> orderItemsDTO = orderItemsResponse.stream()
                .map(response -> {
                    OrderItemDTO orderItemDTO = new OrderItemDTO();
                    orderItemDTO.setProductID(response.getId());
                    orderItemDTO.setProductName(response.getName());
                    orderItemDTO.setProductPhoto(response.getImage());
                    orderItemDTO.setQuantity(response.getQuantity());
                    orderItemDTO.setPrice(response.getPrice());
                    orderItemDTO.setOrderID(orderDTO1.getId());
                    return orderItemDTO;
                })
                .collect(Collectors.toList());
        orderDTO1.setOrderItems(orderItemsDTO);
        saveOrder(orderDTO1);

        // transactions for invoice value withdraw and deposit
        TransactionRequestModel withdrawTransactionModel = orderRequestModel.getTransactionRequestModel();
        withdrawTransactionModel.setAmount(invoiceAmount);
        ResponseEntity<Void> withdrawTransactionResponse = client.withdrawInvoiceAmountFromGuestBankAccount(withdrawTransactionModel);
        if (withdrawTransactionResponse.getStatusCode() != HttpStatus.OK)
            throw new FailedPaymentTransactionException(Messages.FAILED_WITHDRAW_PAYMENT_TRANSACTION.getMessage());

        TransactionRequestModel depositTransactionModel = new TransactionRequestModel();
        depositTransactionModel.setAmount(invoiceAmount);
        depositTransactionModel.setCardNumber(systemBankNumber);
        ResponseEntity<Void> depositTransactionResponse = client.depositInvoiceAmountIntoMerchantBankAccount(depositTransactionModel);
        if (depositTransactionResponse.getStatusCode() != HttpStatus.OK)
            throw new FailedPaymentTransactionException(Messages.FAILED_DEPOSIT_PAYMENT_TRANSACTION.getMessage());

//         send order notifications
//        client.sendOrderDetailsToNotificationsAPI(orderDTO);
        return orderDTO1;
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
        List<Order> orders = orderRepository.findOrdersByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(from, to).orElseGet(null);
        return orders.stream().map(order -> orderMapper.toDTO(order)).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> findAllOrders(){
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(order -> orderMapper.toDTO(order)).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> findOrdersByCreatedAtStartingFrom(Date from) {
        List<Order> orders = orderRepository.findOrdersByCreatedAtGreaterThanEqual(from).orElseGet(null);
        return orders.stream().map(order -> orderMapper.toDTO(order)).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> findOrdersByCreatedAtEndingAt(Date to) {
        List<Order> orders = orderRepository.findOrdersByCreatedAtLessThanEqual(to).orElseGet(null);
        return orders.stream().map(order -> orderMapper.toDTO(order)).collect(Collectors.toList());
    }
}
