package com.lie.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FilterConfig {
    @Bean
    public RouteLocator getewayRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r->r.path("/chat/**")
                     //   .filters(f->)
                        .uri("http://localhost:8080")
                )
                .build();
    }
}
