package com.example.orderapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {
  @Bean
  public WebClient couponApiClient() {
    return WebClient.builder()
      .baseUrl("COUPON_API_URL")
      .build();
  }

  @Bean
  public WebClient storeApiClient() {
    return WebClient.builder()
            .baseUrl("STORE_API_URL")
            .build();
  }

  @Bean
  public WebClient bankApiClient() {
    return WebClient.builder()
            .baseUrl("BANK_API_URL")
            .build();
  }

  @Bean
  public WebClient notificationApiClient() {
    return WebClient.builder()
            .baseUrl("NOTIFICATION_API_URL")
            .build();
  }
}