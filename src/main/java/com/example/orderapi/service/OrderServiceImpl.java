package com.example.orderapi.service;

import com.example.orderapi.dto.*;
import com.example.orderapi.entity.Order;
import com.example.orderapi.mapper.OrderMapper;
import com.example.orderapi.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final WebClientService webClientService;

    @Value("${system_bank_number}")
    private String systemBankNumber;
    @Value("${system_bank_cvv}")
    private String bankAccountCvv;

    @Override
    public OrderDTO createOrder(OrderRequestModel orderRequestModel) {
        checkIfCouponIsValidIfExists(orderRequestModel.getCouponCode(), orderRequestModel.getGuestEmail());
        webClientService.checkIfProductsOutOfStock(orderRequestModel.getOrderRequestItems());
        List<ProductResponse> productResponses = webClientService.getProducts(orderRequestModel.getProductIds());
        BigDecimal invoiceAmount = calculateInvoiceAmount(orderRequestModel.getOrderRequestItems(), productResponses);
        BigDecimal invoiceAmountAfterDiscount = addDiscountIfCouponExists(invoiceAmount, orderRequestModel.getCouponCode(), orderRequestModel.getGuestEmail());
        applyPaymentTransactions(orderRequestModel.getTransactionRequestModel(), invoiceAmountAfterDiscount);
        webClientService.consumeStock(orderRequestModel.getOrderRequestItems());
        OrderDTO orderDTO = createOrder(orderRequestModel, productResponses, invoiceAmountAfterDiscount);
        consumeCouponIfCouponExists(
                orderRequestModel.getCouponCode()
                , orderRequestModel.getGuestEmail()
                , invoiceAmount, orderDTO.getId());
        webClientService.sendOrderDetailsToNotificationsAPI(orderDTO);
        return orderDTO;
    }

    @Override
    public List<OrderDTO> findOrdersByGuestEmail(String guestEmail) {
        List<Order> orders = orderRepository.findOrdersByGuestEmail(guestEmail);
        return orders.stream().map(orderMapper::toDTO).toList();
    }

    @Override
    public List<OrderDTO> findOrdersByCreatedAtBetween(Date from, Date to) {
        List<Order> orders = orderRepository.findOrdersByCreatedAtBetween(from, to);
        return orders.stream().map(orderMapper::toDTO).toList();
    }

    private void checkIfCouponIsValidIfExists(String couponCode, String customerEmail) {
        if (couponCode != null)
            webClientService.validateCoupon(couponCode, customerEmail);
    }

    private BigDecimal calculateInvoiceAmount(List<ItemRequest> itemRequests, List<ProductResponse> productResponses) {
        Map<Long, BigDecimal> productPriceMap = productResponses.stream()
                .collect(Collectors.toMap(ProductResponse::getId, ProductResponse::getPrice));
        return itemRequests.stream()
                .map(orderRequestItem -> {
                    BigDecimal price = productPriceMap.get(orderRequestItem.getProductId());
                    return price.multiply(BigDecimal.valueOf(orderRequestItem.getQuantity()));
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal addDiscountIfCouponExists(BigDecimal invoiceAmount, String couponCode, String customerEmail) {
        if (couponCode != null)
            return invoiceAmount.subtract(webClientService.calculateDiscount(couponCode, customerEmail, invoiceAmount));
        return invoiceAmount;
    }

    private void applyPaymentTransactions(TransactionRequestModel withdrawRequestModel, BigDecimal invoiceAmount) {
        if(invoiceAmount.compareTo(BigDecimal.ZERO) == 0)
            return;
        withdrawRequestModel.setAmount(invoiceAmount);
        webClientService.withdrawInvoiceAmountFromGuestBankAccount(withdrawRequestModel);
        TransactionRequestModel depositRequestModel = TransactionRequestModel.builder()
                .cardNumber(systemBankNumber)
                .cvv(bankAccountCvv)
                .build();
        depositRequestModel.setAmount(invoiceAmount);
        webClientService.depositInvoiceAmountIntoMerchantBankAccount(depositRequestModel);
    }

    private List<OrderItemDTO> createOrderItems(List<ItemRequest> itemRequests, List<ProductResponse> productResponses, Long orderId) {
        Map<Long, BigDecimal> productPriceMap = productResponses.stream()
                .collect(Collectors.toMap(ProductResponse::getId, ProductResponse::getPrice));
        return itemRequests.stream()
                .map(orderRequestItem -> {
                    OrderItemDTO orderItemDTO = new OrderItemDTO();
                    orderItemDTO.setOrderId(orderId);
                    orderItemDTO.setProductId(orderRequestItem.getProductId());
                    orderItemDTO.setQuantity(orderRequestItem.getQuantity());
                    orderItemDTO.setPrice(productPriceMap.get(orderRequestItem.getProductId()));
                    return orderItemDTO;
                }).toList();
    }

    private OrderDTO createOrder(OrderRequestModel orderRequestModel, List<ProductResponse> productResponses, BigDecimal invoiceAmount) {
        OrderDTO orderDTO = OrderDTO.builder()
                .guestEmail(orderRequestModel.getGuestEmail())
                .couponCode(orderRequestModel.getCouponCode())
                .amount(invoiceAmount)
                .build();
        orderDTO = saveOrder(orderDTO);
        orderDTO.setOrderItems(
                createOrderItems(orderRequestModel.getOrderRequestItems(), productResponses, orderDTO.getId())
        );
        return saveOrder(orderDTO);
    }

    private OrderDTO saveOrder(OrderDTO orderDTO) {
        return orderMapper.toDTO(orderRepository.save(orderMapper.toEntity(orderDTO)));
    }

    private void consumeCouponIfCouponExists(String couponCode, String customerEmail, BigDecimal invoiceAmount, Long orderId) {
        if (couponCode != null) {
            webClientService.consumeCoupon(CouponRequestDTO.builder()
                    .code(couponCode)
                    .customerEmail(customerEmail)
                    .orderPrice(invoiceAmount)
                    .orderId(orderId)
                    .build());
        }
    }
}
