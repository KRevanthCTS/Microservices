package com.user.service.dto;


    public class CustomerProfileDto {
        private Long userId;
        private String customerName;
        private String email;
        private String phone;
        private String loyaltyTier;
        private int pointsBalance;
        private int lifetimePoints;
        private String communication;
        private String preferences;

        public CustomerProfileDto() {
        }
        public CustomerProfileDto(Long userId, String customerName, String email, String phone, String loyaltyTier,
                                  int pointsBalance, int lifetimePoints, String communication, String preferences) {
            this.userId = userId;
            this.customerName = customerName;
            this.email = email;
            this.phone = phone;
            this.loyaltyTier = loyaltyTier;
            this.pointsBalance = pointsBalance;
            this.lifetimePoints = lifetimePoints;
            this.communication = communication;
            this.preferences = preferences;
        }
        public Long getUserId() {
            return userId;
        }
        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
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

        public String getCommunication() {
            return communication;
        }

        public void setCommunication(String communication) {
            this.communication = communication;
        }

        public String getPreferences() {
            return preferences;
        }

        public void setPreferences(String preferences) {
            this.preferences = preferences;
        }

        
}