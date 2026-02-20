package com.cts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Analytics Service API")
                        .version("1.0.0")
                        .description("Provides dashboard analytics, KPIs, trends, and report generation")
                        .contact(new Contact().name("Reward360 Team").email("dev@reward360.com")));
    }
}
