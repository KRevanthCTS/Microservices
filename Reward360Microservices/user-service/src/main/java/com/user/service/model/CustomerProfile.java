package com.user.service.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_profile")
public class CustomerProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "loyalty_tier", nullable = false, length = 20)
    private String loyaltyTier;
    
    @Column(name = "points_balance", nullable = false)
    private int pointsBalance;
    
    @Column(name = "lifetime_points", nullable = false)
    private int lifetimePoints; // Total points earned in lifetime
    
    @Column(name = "next_expiry")
    private LocalDate nextExpiry;
    
    @Column(name = "preferences", length = 500)
    private String preferences; // CSV of categories
    
    @Column(name = "communication", length = 50)
    private String communication; // Email/SMS/WhatsApp
    
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    @JsonIgnore
    private User user;
    
    /**
     * JPA lifecycle callback - Updates tier before insert
     * Automatically calculates tier based on lifetime points
     */
    @PrePersist
    public void beforeInsert() {
        updateTierBasedOnLifetimePoints();
    }
    
    /**
     * JPA lifecycle callback - Updates tier before update
     * Automatically recalculates tier based on lifetime points
     */
    @PreUpdate
    public void beforeUpdate() {
        updateTierBasedOnLifetimePoints();
    }
    
    /**
     * Calculate and update tier based on lifetime points
     * Bronze: 0 - 1,999 lifetime points
     * Silver: 2,000 - 4,999 lifetime points
     * Gold: 5,000 - 9,999 lifetime points
     * Platinum: 10,000+ lifetime points
     */
    private void updateTierBasedOnLifetimePoints() {
        if (this.lifetimePoints >= 10000) {
            this.loyaltyTier = "Platinum";
        } else if (this.lifetimePoints >= 5000) {
            this.loyaltyTier = "Gold";
        } else if (this.lifetimePoints >= 2000) {
            this.loyaltyTier = "Silver";
        } else {
            this.loyaltyTier = "Bronze";
        }
    }


    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    public String getLoyaltyTier() {
        return loyaltyTier;
    }
    public void setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }
    public int getPointsBalance() {
        return pointsBalance;
    }
    public void setPointsBalance(int pointsBalance) {
        this.pointsBalance = pointsBalance;
    }
    public int getLifetimePoints() {
        return lifetimePoints;
    }
    public void setLifetimePoints(int lifetimePoints) {
        this.lifetimePoints = lifetimePoints;
    }
    public LocalDate getNextExpiry() {
        return nextExpiry;
    }
    public void setNextExpiry(LocalDate nextExpiry) {
        this.nextExpiry = nextExpiry;
    }
    public String getPreferences() {
        return preferences;
    }
    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
    public String getCommunication() {
        return communication;
    }
    public void setCommunication(String communication) {
        this.communication = communication;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public CustomerProfile() {
    }
}
