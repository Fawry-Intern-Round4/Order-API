package com.example.orderapi.service;

import com.example.orderapi.Client;
import com.example.orderapi.DTO.*;
import com.example.orderapi.entity.Order;
import com.example.orderapi.mapper.OrderMapper;
import com.example.orderapi.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    //TODO: add entity to order items colums (orderid, product id, quantity, price)
    @Override
    public OrderDTO createOrder(String couponCode, List<OrderItemRequest> orderRequestItems) {
        //TODO: check if CouponCode is valid

        //TODO: consume coupon form coupon api

        //TODO: consume product form store api if there is error throw exception

        //TODO: get all products form product api

        //TODO: calculate invoice amount

        //TODO: withdraw invoice amount from guest's bank account

        //TODO: deposit invoice amount to merchant's bank account

        //TODO: send notification to notification api

        Mono<ResponseEntity<List<OrderItemResponse>>> stockResponse = client.consumeProductStock(orderRequestItems);

        BigDecimal invoiceAmount = stockResponse.flatMap(response ->
                        Mono.justOrEmpty(response.getBody())
                                .map(orderItemResponses -> orderItemResponses.stream()
                                        .filter(OrderItemResponse::isAvailable)
                                        .map(orderItemResponse -> orderItemResponse.getPrice()
                                                .multiply(new BigDecimal(orderItemResponse.getQuantity())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                                .defaultIfEmpty(BigDecimal.ZERO))
                .block();

        CouponDTO couponDTO = null;
        if (client.validateCouponCode(couponCode).getBody().equals(true)){
            couponDTO = client.consumeCoupon(couponCode).getBody();
            if (couponDTO.getType().equals("fixed")) invoiceAmount = invoiceAmount.subtract(couponDTO.getValue());
            else invoiceAmount = invoiceAmount.subtract(invoiceAmount.multiply(couponDTO.getValue().divide(BigDecimal.valueOf(100))));
        }
        else {
            //TODO: throw exception
        }
        // withdraw invoice amount from guest's bank account
        TransactionRequestModel withdrawRequestModel = new TransactionRequestModel();
        withdrawRequestModel.setAmount(invoiceAmount);
        // TODO: set field values of transaction request DTO, i.e., cardNumber and cvv.
        client.withdrawInvoiceAmountFromGuestBankAccount(withdrawRequestModel);

        // deposit invoice amount to merchant's bank account
        TransactionRequestModel depositRequestModel = new TransactionRequestModel();
        depositRequestModel.setAmount(invoiceAmount);
        depositRequestModel.setCardNumber(systemBankNumber);
        client.depositInvoiceAmountIntoMerchantBankAccount(depositRequestModel);

        // create and store order
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setAmount(invoiceAmount);
        orderDTO.setCouponID(couponDTO.getId());
        saveOrder(orderDTO);

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
    public void saveOrder(OrderDTO orderDTO) {
        orderRepository.save(orderMapper.toEntity(orderDTO));
    }

    @Override
    public List<OrderDTO> findOrdersByCreatedAtBetween(Date from, Date to) {
        List<Order> orders = orderRepository.findOrdersByCreatedAtBetween(from, to).orElseGet(null);
        return orders.stream().map(order -> orderMapper.toDTO(order)).collect(Collectors.toList());
    }
}
