package com.user.service.client;

import com.user.service.dto.CustomerProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "CustomerMs")
public interface CustomerServiceClient {

    @PostMapping("/api/users/addcustomer")
    void createProfile(@RequestBody CustomerProfileDto profileDto);

    @PostMapping("/api/users/redeem/offer/{offerId}/user/{userId}")
    Object redeemOffer(@RequestBody Object redeemRequest, @PathVariable("userId") Long userId, @PathVariable("offerId") Long offerId);

    @PostMapping("/api/users/claim/user/{userId}")
    Object claimPoints(@RequestBody Object claimRequest, @PathVariable("userId") Long userId);

    @GetMapping("/api/users/transactions/user/{userId}")
    java.util.List<java.util.Map<String, Object>> getTransactions(@PathVariable("userId") Long userId);

    @GetMapping("/api/users/Customer/{id}")
    com.user.service.dto.CustomerProfileDto getCustomer(@PathVariable("id") Long id);

    @GetMapping("/api/users/offers/teir/{tier}")
    java.util.List<java.util.Map<String, Object>> getOffersByTier(@PathVariable("tier") String tier);

    @GetMapping("/api/users/redemptions/user/{userId}")
    java.util.List<java.util.Map<String, Object>> getRedemptions(@PathVariable("userId") Long userId);
}
