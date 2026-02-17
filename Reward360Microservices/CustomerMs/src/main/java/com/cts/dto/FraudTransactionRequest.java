package com.cts.dto;
 
import java.time.LocalDate;
 
public class FraudTransactionRequest {
    private String externalId;
    private String accountId;
    private String type;
    private Integer pointsEarned;
    private Integer pointsRedeemed;
    private String store;
    private LocalDate date;
    private LocalDate expiry;
    private String note;
    private Long userId;
 
    // Getters and Setters
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
 
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
 
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
}
 
 

 