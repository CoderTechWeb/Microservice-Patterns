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
                        .filters(f -> f.circuitBreaker(c -> c.setName("orderServiceCB")
                                .setFallbackUri("forward:/fallback/orderFallback"))) // Fallback method
                        .uri("lb://ORDER-SERVICE"))

                .route("payment-service", r -> r.path("/payment/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("paymentServiceCB")
                                .setFallbackUri("forward:/fallback/paymentFallback"))) // Fallback method
                        .uri("lb://PAYMENT-SERVICE"))
            .build();
    }
}
