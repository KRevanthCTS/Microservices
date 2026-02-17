package com.user.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

// Example Feign Client - connect to other services here
@FeignClient(name = "rewards-service", url = "http://localhost:8080") // Assuming monolith runs on 8080
public interface RewardsClient {

    @GetMapping("/api/rewards") // Example endpoint
    String getRewards();
}
