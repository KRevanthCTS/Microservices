package com.cts.Feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import com.cts.dto.Redemption;

// CustomerServiceClient.java
@FeignClient(name = "CustomerMs") // Matches Gateway lb://CustomerMs
public interface CustomerServiceClient {
    @GetMapping("/api/users/redemptions")
    List<Redemption> getAllRedemptions();
}