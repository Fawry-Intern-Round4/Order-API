package com.example.orderapi.mapper;

import com.example.orderapi.DTO.OrderDTO;
import com.example.orderapi.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toEntity(OrderDTO orderDTO);

    OrderDTO toDTO(Order order);
}
