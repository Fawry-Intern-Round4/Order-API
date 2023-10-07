package com.example.orderapi.repository.config;

import com.example.orderapi.repository.OrderRepository;
import com.example.orderapi.repository.entity.Order;
import com.example.orderapi.repository.entity.Product;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackageClasses = {Order.class, Product.class})
@EnableJpaRepositories(basePackageClasses = OrderRepository.class)
public class RepositoryConfig {

}