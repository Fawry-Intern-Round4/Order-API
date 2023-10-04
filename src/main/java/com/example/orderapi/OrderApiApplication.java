package com.example.orderapi;

import com.example.orderapi.config.WebConfig;
import com.example.orderapi.repository.config.RepositoryConfig;
import com.example.orderapi.rest.Config.RestConfig;
import com.example.orderapi.service.config.ServiceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
        RepositoryConfig.class,
        ServiceConfig.class,
        RestConfig.class,
        WebConfig.class
})
public class OrderApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApiApplication.class, args);
    }
}
