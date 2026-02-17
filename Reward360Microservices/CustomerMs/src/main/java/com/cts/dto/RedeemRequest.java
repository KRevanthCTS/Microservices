
package com.cts.dto;

public class RedeemRequest{

    Long offerId;
    String store;

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }
    public RedeemRequest() {
    }
    
    public RedeemRequest(Long offerId, String store) {
        this.offerId = offerId;
        this.store = store;
    }
    
}