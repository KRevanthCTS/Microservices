package com.example.dashboard_backend.entity;
 
import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
 
@Entity
@Table(name = "transactions")
public class Transaction {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    // Business identifiers
    @Column(name = "merchant_id")
    private String transactionId;
 
    @Column(name = "transaction_id")
    private String externalId; // For rewards transactions
 
    private String accountId;
 
 
    // Context (for fraud detection)
    private String paymentMethod;    // e.g., "CARD", "UPI", "NETBANKING"
 
    @Column(length = 2000)
    private String description;
 
    // Risk & status for the screen
    private String riskLevel; // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    private String status = "CLEARED"; // "CLEARED", "REVIEW", "BLOCKED"
 
    // Rewards-specific fields
    private String type; // "CLAIM", "REDEMPTION", etc.
    private Integer pointsEarned;
    private Integer pointsRedeemed;
    private String store;
    private LocalDate date;
    private LocalDate expiry;
    private String note;
 
 
    // User ID reference (without entity relationship for now)
    @Column(name = "user_id")
    private Long userId;
 
    // Timestamps
    @Column(nullable = false)
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
 
    // --- getters & setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
 
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
 
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
 
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
 
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
 
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
 
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
 
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
 
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
 
    public Integer getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(Integer pointsEarned) { this.pointsEarned = pointsEarned; }
 
    public Integer getPointsRedeemed() { return pointsRedeemed; }
    public void setPointsRedeemed(Integer pointsRedeemed) { this.pointsRedeemed = pointsRedeemed; }
 
    public String getStore() { return store; }
    public void setStore(String store) { this.store = store; }
 
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
 
    public LocalDate getExpiry() { return expiry; }
    public void setExpiry(LocalDate expiry) { this.expiry = expiry; }
 
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
 
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
 
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
 
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
 
}
 
 
 