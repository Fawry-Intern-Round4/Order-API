package com.example.orderapi.service.config;

import com.example.orderapi.service.OrderService;
import com.example.orderapi.service.mapper.OrderMapper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {OrderService.class, OrderMapper.class})
public class ServiceConfig {
}
