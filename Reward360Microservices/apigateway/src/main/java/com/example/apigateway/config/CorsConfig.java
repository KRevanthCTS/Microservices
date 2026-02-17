package com.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. MUST be true if you are using Sessions, Cookies, or Authorization Headers
        config.setAllowCredentials(true);

        // 2. Be explicit with Origins. Wildcards like "*" or "*" with patterns 
        // can sometimes be rejected by browsers when credentials are true.
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000", // Common React port
            "http://localhost:5173", // Common Vite port
            "http://127.0.0.1:3000",
            "http://127.0.0.1:5173"
        ));

        // 3. Allowed Methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 4. Specific Allowed Headers (Better for security/stability than "*")
        config.setAllowedHeaders(Arrays.asList(
            "Origin", 
            "Content-Type", 
            "Accept", 
            "Authorization", 
            "X-Requested-With"
        ));

        // 5. Exposed Headers
        config.setExposedHeaders(Arrays.asList(
            "Authorization", 
            "Access-Control-Allow-Origin", 
            "Access-Control-Allow-Credentials"
        ));

        // 6. Pre-flight cache
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}