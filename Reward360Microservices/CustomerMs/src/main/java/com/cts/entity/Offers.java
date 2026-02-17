// package com.cts.entity;

// import jakarta.persistence.Entity;
// import jakarta.persistence.Table;
// import jakarta.persistence.Id;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;

// @Entity
// @Table (name="Offer")
// public class Offers{
//      @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
//     private String title;
//     private String category;
//     private String description;
//     private int costPoints;
//     private String imageUrl;
//     private boolean active;
//     private String tier;

//     // Getters and Setters

//     public Long getId() {
//         return id;
//     }       
//     public void setId(Long id) {
//         this.id = id;
//     }
//     public String getTier() {
//         return tier;
//     }
//     public void setTier(String tier) {
//         this.tier = tier;
//     }
    
//     public String getTitle() {
//         return title;
//     }
//     public void setTitle(String title) {
//         this.title = title;
//     }
//     public String getCategory() {
//         return category;
//     }
//     public void setCategory(String category) {
//         this.category = category;
    
//     }
//     public String getDescription() {
//         return description;
//     }
//     public void setDescription(String description) {
//         this.description = description;
//     }
//     public int getCostPoints() {
//         return costPoints;
//     }
//     public void setCostPoints(int costPoints) {
//         this.costPoints = costPoints;
//     }
//     public String getImageUrl() {
//         return imageUrl;
//     }
//     public void setImageUrl(String imageUrl) {
//         this.imageUrl = imageUrl;
//     }
//     public boolean isActive() {
//         return active;
//     }
//     public void setActive(boolean active) {
//         this.active = active;
//     }
//     public Offers() {
//     }
//     public Offers(Long id, String title, String category, String description, int costPoints, String imageUrl,
//             boolean active, String tier) {
//         this.id = id;
//         this.title = title;
//         this.category = category;
//         this.description = description;
//         this.costPoints = costPoints;
//         this.tier = tier;
//         this.imageUrl = imageUrl;
//         this.active = active;
//     }
// }