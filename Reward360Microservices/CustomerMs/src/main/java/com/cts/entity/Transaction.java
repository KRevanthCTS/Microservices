package com.cts.entity;

import java.time.LocalDate;


import jakarta.persistence.*;

@Entity
@Table(name="Transaction")
public class Transaction {
        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String externalId;
    private String type; // PURCHASE, REDEMPTION, CLAIM
    private int pointsEarned;
    private int pointsRedeemed;
    private String store;
    private LocalDate date;
    private LocalDate expiry;
    private String note;
    private Long userId; // Store user ID instead of User object
    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public int getPointsRedeemed() {
        return pointsRedeemed;
    }

    public void setPointsRedeemed(int pointsRedeemed) {
        this.pointsRedeemed = pointsRedeemed;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getExpiry() {
        return expiry;
    }

    public void setExpiry(LocalDate expiry) {
        this.expiry = expiry;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Constructors
    public Transaction() {
    }

    public Transaction(Long id, String externalId, String type, int pointsEarned, int pointsRedeemed, String store,
            LocalDate date, LocalDate expiry, String note, Long userId) {
        this.id = id;
        this.externalId = externalId;
        this.type = type;
        this.pointsEarned = pointsEarned;
        this.pointsRedeemed = pointsRedeemed;
        this.store = store;
        this.date = date;
        this.expiry = expiry;
        this.note = note;
        this.userId = userId;
    }
}
