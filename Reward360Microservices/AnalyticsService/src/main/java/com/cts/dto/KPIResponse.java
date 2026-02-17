package com.cts.dto;


public class KPIResponse {
    private long users;
    private long offers;
    private long redemptions;
    private double redemptionRate;

    public KPIResponse(long users, long offers, long redemptions, double redemptionRate) {
        this.users = users;
        this.offers = offers;
        this.redemptions = redemptions;
        this.redemptionRate = redemptionRate;
    }
    public long getUsers() {
        return users;
    }
    public void setUsers(long users) {
        this.users = users;
    }
    public long getOffers() {
        return offers;
    }
    public void setOffers(long offers) {
        this.offers = offers;
    }
    public long getRedemptions() {
        return redemptions;
    }
    public void setRedemptions(long redemptions) {
        this.redemptions = redemptions;
    }
    public double getRedemptionRate() {
        return redemptionRate;
    }
    public void setRedemptionRate(double redemptionRate) {
        this.redemptionRate = redemptionRate;
    }
        @Override
    public String toString() {
        return "KPIResponse{" +
                "users=" + users +
                ", offers=" + offers +
                ", redemptions=" + redemptions +
                ", redemptionRate=" + redemptionRate +
                '}';
    }
}
    