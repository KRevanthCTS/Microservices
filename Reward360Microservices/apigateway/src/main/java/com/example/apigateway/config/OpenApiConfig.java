package com.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Reward360 API Gateway")
                        .version("1.0.0")
                        .description("Centralized API Gateway — routes to all Reward360 microservices. "
                                + "Use the dropdown at the top to switch between services.")
                        .contact(new Contact().name("Reward360 Team").email("dev@reward360.com")));
    }
}
