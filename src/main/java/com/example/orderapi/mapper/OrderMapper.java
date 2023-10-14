package com.example.orderapi.mapper;

import com.example.orderapi.dto.OrderDTO;
import com.example.orderapi.dto.OrderItemDTO;
import com.example.orderapi.entity.Order;
import com.example.orderapi.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
    Order toEntity(OrderDTO orderDTO);

    OrderDTO toDTO(Order order);

    OrderItem toEntity(OrderItemDTO orderItemDTO);

    OrderItemDTO toDTO(OrderItem orderItem);
}
