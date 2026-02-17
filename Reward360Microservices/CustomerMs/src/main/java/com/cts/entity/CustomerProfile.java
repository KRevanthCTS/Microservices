package com.cts.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

// In Customer Service: com.cts.entity.CustomerProfile
@Entity
@Table(name="CustomerProfile")
public class CustomerProfile {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // CHANGE: Replace 'private User user' with this
    private Long userId; 
    
    private String customerName; // Added to store the name locally
    private String loyaltyTier;
    private int pointsBalance;
    private int lifetimePoints;
    private LocalDate nextExpiry;
    private String preferences;
    private String communication; 

    // Standard Getters and Setters...


    // Getters and Setters
    public int getLifetimePoints() {
        return lifetimePoints;
    }
    public void setLifetimePoints(int lifetimePoints) {
        this.lifetimePoints = lifetimePoints;
    }
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
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public CustomerProfile() {
    }
    public CustomerProfile(Long id, String loyaltyTier, int pointsBalance, LocalDate nextExpiry, String preferences,
            String communication, Long userId) {
        this.id = id;
        this.loyaltyTier = loyaltyTier;
        this.pointsBalance = pointsBalance;
        this.nextExpiry = nextExpiry;
        this.preferences = preferences;
        this.communication = communication;
        this.userId = userId;
    }   

}
