package com.example.orderapi.repository.config;

import com.example.orderapi.repository.OrderRepository;
import com.example.orderapi.repository.entity.Order;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackageClasses = Order.class)
@EnableJpaRepositories(basePackageClasses = OrderRepository.class)
public class RepositoryConfig {

}