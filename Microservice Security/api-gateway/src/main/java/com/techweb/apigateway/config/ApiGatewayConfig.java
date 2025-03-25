package com.techweb.apigateway.config;

import com.techweb.apigateway.filter.JwtAuthFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public ApiGatewayConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**").uri("lb://AUTH-SERVICE"))
                .route("order-service", r -> r.path("/order/**")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("lb://ORDER-SERVICE"))
                .build();
    }
}