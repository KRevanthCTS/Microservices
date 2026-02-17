package com.example.Offers.controller;  

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Offers.model.Analytics;
import com.example.Offers.model.Promotion;
import com.example.Offers.repository.AnalyticsRepository;
import com.example.Offers.service.PromotionService;

@RestController
@RequestMapping("/api/promotions")

public class PromotionController {

    @Autowired
    private PromotionService service;

    @Autowired
    private AnalyticsRepository analyticsRepo;

    @Autowired
    private com.example.Offers.repository.PromotionRepository promotionRepository;
    /**
     * Saves a new promotion and automatically triggers
     * the creation of an associated analytics record.
     */
    @PostMapping("/promotions")
    public ResponseEntity<Promotion> createPromotion(@RequestBody Promotion promotion) {
        Promotion savedPromotion = service.savePromotionWithAnalytics(promotion);
        return ResponseEntity.ok(savedPromotion);
    }

    /**
     * Retrieves all promotion records.
     */
    @GetMapping("/promotions")
    public List<Promotion> getAllPromotions() {
        return service.getAllPromotions();
    }
     @GetMapping("/api/promotions/{id}")
    public ResponseEntity<Promotion> getOfferById(@PathVariable("id") Long id) {
        Promotion promotion = service.getPromotionById(id);
        if (promotion != null) {
            return ResponseEntity.ok(promotion);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all data from the analytics table.
     */
    @GetMapping("/analytics")
    public List<Analytics> getAnalyticsRecords() {
        return analyticsRepo.findAll();
    }

    @DeleteMapping("/promotions/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        service.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

    // New endpoint to toggle the active status of a promotion for publish and unpublish
    @PutMapping("/promotions/{id}")
        public ResponseEntity<Promotion> togglePromotionActive(@PathVariable("id") Long id){
        Promotion ex = promotionRepository.findById(id).get();
                Promotion p = ex;
        p.setActive(!p.getActive());
        Promotion saved = promotionRepository.save(p);
        return ResponseEntity.ok(saved);
    }
}