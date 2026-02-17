package com.example.Offers.model;
import jakarta.persistence.*;


import java.time.LocalDate;

@Entity
@Table(name = "promotions")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    // This ensures JSON "cost" maps to Java "costPoints"
 
    private Integer costPoints;

    private LocalDate startDate;
    private LocalDate endDate;

 @Column(name = "active", nullable = false)
    private boolean active;
    private String tierLevel;

    private String imageUrl;

    public boolean   getActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCostPoints() {
        return costPoints;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getTierLevel() {
        return tierLevel;
    }

    public String getImageUrl() {
        return imageUrl;
    }

        public void setId(Long id) {
            this.id = id;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setCostPoints(Integer costPoints) {
            this.costPoints = costPoints;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public void setTierLevel(String tierLevel) {
            this.tierLevel = tierLevel;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
        public Promotion() {}
        public Promotion(String title, String category, String description, Integer costPoints,
                         LocalDate startDate, LocalDate endDate, String tierLevel, String imageUrl) {
            this.title = title;
            this.category = category;
            this.description = description;
            this.costPoints = costPoints;
            this.startDate = startDate;
            this.endDate = endDate;
            this.tierLevel = tierLevel;
            this.imageUrl = imageUrl;
        }

}