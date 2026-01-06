package com.example.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service-route", r -> r.path("/users/**")
                        .filters(f -> f.circuitBreaker(config -> config
                                        .setName("userServiceCircuit")
                                        .setFallbackUri("forward:/fallback/users"))
                                .stripPrefix(1))
                        .uri("lb://user-service"))
                .build();
    }
}
