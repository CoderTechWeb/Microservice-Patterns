package com.techweb.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("order-service", r -> r.path("/order/**")
                .uri("lb://ORDER-SERVICE"))
            .route("payment-service", r -> r.path("/payment/**")
                .uri("lb://PAYMENT-SERVICE"))
            .route("inventory-service", r -> r.path("/inventory/**")
                .uri("lb://INVENTORY-SERVICE"))
            .build();
    }
}
