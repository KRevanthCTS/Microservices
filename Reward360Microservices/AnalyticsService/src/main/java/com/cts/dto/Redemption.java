package com.cts.dto;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.persistence.Table;
import jakarta.persistence.Id;

public class Redemption {
    private Long id;
    private String confirmationCode;
    private String transactionId;
    private LocalDate date;
    private int costPoints;
    private String offerTitle;
    private String store;

    private Long userId; // Store user ID instead of User object
    // Getters and Setters
    public Long getId() {   
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getConfirmationCode() {
        return confirmationCode;
    }
    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public int getCostPoints() {
        return costPoints;
    }
    public void setCostPoints(int costPoints) {
        this.costPoints = costPoints;
    }
    public String getOfferTitle() {
        return offerTitle;
    }
    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }
    public String getStore() {
        return store;
    }
    public void setStore(String store) {
        this.store = store;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    public Redemption() {
    }
    public Redemption(Long id, String confirmationCode, String transactionId, LocalDate date, int costPoints,
            String offerTitle, String store, Long userId) {
        this.id = id;
        this.confirmationCode = confirmationCode;
        this.transactionId = transactionId;
        this.date = date;
        this.costPoints = costPoints;
        this.offerTitle = offerTitle;
        this.store = store;
        this.userId = userId;
    }
}
