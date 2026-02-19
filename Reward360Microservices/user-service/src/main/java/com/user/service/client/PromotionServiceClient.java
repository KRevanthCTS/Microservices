package com.user.service.client;

import com.user.service.dto.PromotionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "Promotionservice", url = "http://localhost:8083")
public interface PromotionServiceClient {
    @GetMapping("/api/promotions/promotions")
    List<PromotionDto> getAllPromotions();

    @GetMapping("/api/promotions/promotions/{id}")
    PromotionDto getPromotionById(@PathVariable("id") Long id);
}
