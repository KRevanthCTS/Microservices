package com.example.eureka_discovery_space;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaDiscoverySpaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaDiscoverySpaceApplication.class, args);
    }
}
