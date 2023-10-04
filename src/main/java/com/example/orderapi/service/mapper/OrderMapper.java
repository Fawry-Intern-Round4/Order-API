package com.example.orderapi.service.mapper;

import com.example.orderapi.repository.entity.Order;
import com.example.orderapi.model.OrderModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    public OrderModel toModel(Order order);

    public Order toEntity(OrderModel orderModel);
}
