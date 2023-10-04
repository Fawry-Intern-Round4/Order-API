package com.example.orderapi.rest.Config;

import com.example.orderapi.rest.OrderController;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {OrderController.class})
public class RestConfig {
}
