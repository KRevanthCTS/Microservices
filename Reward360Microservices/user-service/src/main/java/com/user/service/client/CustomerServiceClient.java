package com.user.service.client;

import com.user.service.dto.CustomerProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "CustomerMs")
public interface CustomerServiceClient {
    @PostMapping("/api/users/addcustomer")
    void createProfile(@RequestBody CustomerProfileDto profileDto);
}
