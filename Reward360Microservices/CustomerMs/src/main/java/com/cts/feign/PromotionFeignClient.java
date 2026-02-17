package com.cts.feign;

import com.cts.dto.OfferDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "Promotionservice") 
public interface PromotionFeignClient {

    // Final URL: /api/promotions + /promotions = /api/promotions/promotions
    @GetMapping("/api/promotions/promotions")
    List<OfferDto> getAllOffers();

    // Final URL: /api/promotions + /api/promotions/{id} = /api/promotions/api/promotions/{id}
    @GetMapping("/api/promotions/api/promotions/{id}")
    OfferDto getOfferById(@PathVariable("id") Long id);
}