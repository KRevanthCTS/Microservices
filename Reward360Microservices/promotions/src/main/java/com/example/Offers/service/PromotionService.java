package com.example.Offers.service; 
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Offers.model.Analytics;
import com.example.Offers.model.Promotion;
import com.example.Offers.repository.AnalyticsRepository;
import com.example.Offers.repository.PromotionRepository;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepo;

    @Autowired
    private AnalyticsRepository analyticsRepo;

    /**
     * Saves the promotion and immediately mirrors the
     * ID, Title, Category, and CostPoints into the Analytics table.
     */
    @Transactional
    public Promotion savePromotionWithAnalytics(Promotion promotion) {
        // Step 1: Save the Promotion to generate the primary key (ID)
        Promotion savedPromo = promotionRepo.save(promotion);

        // Step 2: Pass the saved object to the Analytics constructor
        // This maps: promotionId, title, category, and costPoints
        Analytics analytics = new Analytics(savedPromo);

        // Step 3: Persist the analytics record
        analyticsRepo.save(analytics);

        return promotion;
    }

    /**
     * Retrieves all promotions from the database.
     */
    public List<Promotion> getAllPromotions() {
        return promotionRepo.findAll();
    }

    // Added method to fetch a promotion by ID
    public Promotion getPromotionById(Long id) {
        return promotionRepo.findById(id).orElse(null);
    }

    // Added method to delete a promotion by ID
    public void deletePromotion(Long id) {
        promotionRepo.deleteById(id);
    }
}