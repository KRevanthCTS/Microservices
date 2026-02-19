package com.cts.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cts.dto.OfferDto;

@FeignClient(name = "Promotionservice")
public interface PromotionFeignClient {

    // Final URL: /api/promotions + /promotions = /api/promotions/promotions
    @GetMapping("/api/promotions/promotions")
    List<OfferDto> getAllOffers();

    // Full path: /api/promotions/promotions/{id}
    @GetMapping("/api/promotions/promotions/{id}")
    OfferDto getOfferById(@PathVariable("id") Long id);
}