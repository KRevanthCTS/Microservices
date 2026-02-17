package com.example.Offers.model;

import jakarta.persistence.*;
@Entity
@Table(name = "analytics")
public class Analytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long promotionId; // Keep this to link back to the original promo
    private String title;
    private String category;
    private Integer costPoints;

    // Mapping Constructor: Matches only the fields you want
    public Analytics(Promotion promo) {
        this.promotionId = promo.getId();
        this.title = promo.getTitle();
        this.category = promo.getCategory();
        this.costPoints = promo.getCostPoints();
    }

    // Default constructor for JPA
    public Analytics() {}   
        public Long getId() {
        return id;
        }
        public Long getPromotionId() {
        return promotionId;
        }
        public String getTitle() {
        return title;
        }
        public String getCategory() {
        return category;
        }
        public Integer getCostPoints() {
        return costPoints;
        }

}