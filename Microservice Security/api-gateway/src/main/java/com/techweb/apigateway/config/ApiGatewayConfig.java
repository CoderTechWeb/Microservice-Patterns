package com.techweb.apigateway.config;

import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Configuration
public class ApiGatewayConfig {


    private static final String BASIC_AUTH_CREDENTIALS = "user:password"; // Change as needed
    private static final String BASIC_AUTH_HEADER = "Basic " + Base64.getEncoder().encodeToString(BASIC_AUTH_CREDENTIALS.getBytes());

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**").uri("lb://AUTH-SERVICE"))
                .route("order-service", r -> r.path("/order/**")
                        .filters(f -> f.modifyRequestBody(String.class, String.class, (exchange, s) -> {
                            return Mono.just(s);
                        }).filter(this::addBasicAuthHeader))
                        .uri("lb://ORDER-SERVICE"))
                .build();
    }

    private Mono<Void> addBasicAuthHeader(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
                .build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Bean
    public DiscoveryClientRouteDefinitionLocator discoveryClientRouteLocator(ReactiveDiscoveryClient discoveryClient, DiscoveryLocatorProperties properties) {
        return new DiscoveryClientRouteDefinitionLocator(discoveryClient, properties);
    }
}
