package com.example.orderapi.service.config;

import com.example.orderapi.service.OrderService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {OrderService.class})
public class ServiceConfig {
}
