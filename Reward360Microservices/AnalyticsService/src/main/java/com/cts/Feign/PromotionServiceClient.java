package com.cts.Feign;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

import com.cts.dto.Promotion;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "Promotionservice") // Matches Gateway lb://Promotionservice
public interface PromotionServiceClient {
    @GetMapping("/api/promotions/promotions")
    List<Promotion> getAllOffers();
}

