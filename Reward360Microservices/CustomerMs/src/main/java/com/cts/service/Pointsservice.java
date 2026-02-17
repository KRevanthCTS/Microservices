package com.cts.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.dto.ClaimRequest;
import com.cts.dto.OfferDto;
import com.cts.dto.RedeemRequest;
import com.cts.entity.CustomerProfile;
import com.cts.entity.Redemption;
import com.cts.entity.Transaction;
import com.cts.feign.PromotionFeignClient;
import com.cts.repository.CustomerProfilerepository;
import com.cts.repository.RedemptionRepository;
import com.cts.repository.TransactionRepository;
import com.cts.feign.FraudDetectionClient;
import com.cts.dto.FraudTransactionRequest;
@Service
public class Pointsservice {

    // Define a constant for initial points assigned to new users normally based on thhe purchase value
    //  we assign points but for now we are assigning a default value so for
    //  1000 rs purchase we are assigning
    //  100 points to the user
    public static final int INITIAL_POINTS = 1000; // Starting points for new users

    @Autowired
    private CustomerProfilerepository customerProfileRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private PromotionFeignClient promotionFeignClient;
    
    @Autowired
    private RedemptionRepository redemptionRepository;

    @Autowired
    private CustomerProfilerepository custrepo;

    @Autowired
    private FraudDetectionClient fraudDetectionClient;  



    public CustomerProfile getCutomerId(Long id) {

        return customerProfileRepository.findByUserId(id);

    }
 
    @Transactional
    public CustomerProfile registerUser(CustomerProfile registerRequest) {
                // Create CustomerProfile entity linked to the User
        CustomerProfile profile = new CustomerProfile();
        profile.setUserId(registerRequest.getUserId()); // Link profile to user ID
        profile.setCustomerName(registerRequest.getCustomerName());
        profile.setLoyaltyTier("BRONZE"); // Default tier
        profile.setPointsBalance(INITIAL_POINTS); // Start with initial points
        profile.setLifetimePoints(INITIAL_POINTS);
        profile.setNextExpiry(LocalDate.now().plusMonths(4)); // Points expire in 4 months
        profile.setPreferences(registerRequest.getPreferences());
        profile.setCommunication(registerRequest.getCommunication());
        
        return custrepo.save(profile);
    }
   


    /**
     * Redeem an offer for a user
     * Deducts points from user's profile and creates a redemption record
     * Fetches offer details from Promotion Service via Feign client
     */
    @Transactional
    public Redemption redeemOffer(Long userId, RedeemRequest request) {
        CustomerProfile profile = custrepo.findByUserId(userId);
        if (profile == null) {
            throw new RuntimeException("Customer profile not found for user: " + userId);
        }
    
        // Fetch specific offer details from Promotion Service via Feign
        OfferDto offer = promotionFeignClient.getOfferById(request.getOfferId());
        
        // CustomerProfile profile = custrepo.findById(userId)
        //     .orElseThrow(() -> new RuntimeException("Customer profile not found for user: " + userId));
        if (profile.getPointsBalance() < offer.getCostPoints()) {
            throw new RuntimeException("Insufficient points");
        }
        
        // Deduct points
        profile.setPointsBalance(profile.getPointsBalance() - offer.getCostPoints());
        
        // Create Transaction
        Transaction transaction = new Transaction();
        transaction.setExternalId("RED-" + System.currentTimeMillis());
        transaction.setType("REDEMPTION");
        transaction.setPointsRedeemed(offer.getCostPoints());
        transaction.setNote(offer.getTitle());
        transaction.setUserId(userId);
        transaction.setStore("Online"); // Assuming online redemption, can be dynamic based on offer
        transaction.setDate(LocalDate.now());
        transactionRepository.save(transaction);


        sendToFraudDetection(transaction);
        // Create Redemption record
        Redemption redemption = new Redemption();
        redemption.setTransactionId(transaction.getExternalId());
        redemption.setConfirmationCode("CONF-" + System.currentTimeMillis());
        redemption.setOfferTitle(offer.getTitle());
        redemption.setCostPoints(offer.getCostPoints());
        redemption.setUserId(userId);
        redemption.setDate(LocalDate.now());
        redemption.setStore("Online"); // Assuming online redemption, can be dynamic based on offer
        redemptionRepository.save(redemption);

        custrepo.save(profile); // Update profile with new points balance
        return redemption;
    }

  private static final List<String> TIER_ORDER = List.of("BRONZE", "SILVER", "GOLD", "PLATINUM");

    public List<OfferDto> getOffersByTier(String userTier) {
        // 1. Get all offers from the other service via Feign
        List<OfferDto> allOffers = promotionFeignClient.getAllOffers();
        
        // 2. Prepare a list to store the results
        List<OfferDto> eligibleOffers = new ArrayList<>();

        // 3. Determine the user's tier rank
        int userTierRank = TIER_ORDER.indexOf(userTier.toUpperCase());
        
        // If the tier is unknown, default to the lowest (Bronze)
        if (userTierRank == -1) {
            userTierRank = 0; 
        }

        // 4. Create a sub-list of all allowed tiers (e.g., ["BRONZE", "SILVER"])
        List<String> allowedTiers = TIER_ORDER.subList(0, userTierRank + 1);

        // 5. Use a standard loop to filter
        for (OfferDto offer : allOffers) {
            if (offer.getTierLevel() != null  && offer.getActive()) { // Check if tier level is not null and offer is active
                String offerTier = offer.getTierLevel().toUpperCase();
                
                // Check if the offer's tier is within the user's allowed list
                if (allowedTiers.contains(offerTier)) {
                    eligibleOffers.add(offer);
                }
            }
        }

        return eligibleOffers;
    }

   /**
     * Claim an offer for a user
     * Only adds points to user's profile and creates a transaction entry
     * Does NOT create a redemption record
     */
    @Transactional
    public ClaimRequest claimOffer(ClaimRequest claimRequest, Long userId) {
        // Fetch user
        CustomerProfile profile = custrepo.findByUserId(userId);
if (profile == null) { throw new RuntimeException("Customer profile not found for user: " + userId); }
        // Add points to profile
        int previousBalance = profile.getPointsBalance();
        profile.setPointsBalance(previousBalance + claimRequest.getPoints());
        profile.setLifetimePoints(profile.getLifetimePoints() + claimRequest.getPoints());
        custrepo.save(profile);
        
        // Create transaction record only (no redemption entry)
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setType("CLAIM");
        transaction.setPointsEarned(claimRequest.getPoints());
        transaction.setPointsRedeemed(0);
        transaction.setStore("Claim");
        transaction.setDate(LocalDate.now());
        transaction.setExpiry(LocalDate.now().plusMonths(4)); // Points expire in 4 months
        transaction.setNote("Claimed: " + claimRequest.getNote());
        String transactionId = "CLM-" + System.currentTimeMillis();
        transaction.setExternalId(transactionId);
        transactionRepository.save(transaction);
        
        // Return response DTO
      
          // Send to Fraud Detection Service
        sendToFraudDetection(transaction);
      
        return claimRequest;
    }

    /**
     * Get all redemptions for a specific user
     */
    public List<Redemption> getRedemptionsByUserId(Long userId) {
        // Verify user exists
       CustomerProfile profile = custrepo.findByUserId(userId);
        if (profile == null) {
            throw new RuntimeException("Customer profile not found for user: " + userId);
        }
            
        
        // Return redemptions sorted by date descending
        return redemptionRepository.findByUserIdOrderByDateDesc(userId);
    }

    /**
     * Get all transactions for a specific user
     */
    public List<Transaction> getTransactionsByUserId(Long userId) {
        // Verify user exists
      CustomerProfile profile = custrepo.findByUserId(userId);
        if (profile == null) {
            throw new RuntimeException("Customer profile not found for user: " + userId);
        }
        // Return transactions for the user sorted by date descending
        return transactionRepository.findByUserIdOrderByDateDesc(userId);
    }




    /// Get all redemptions in the system (for admin view)
     public List<Redemption> getAllRedemptions() {
        return redemptionRepository.findAll();
    }
    /**
     * Get all offers available for a specific tier
     */
    // public List<Offers> getOffersByTier(String tier) {
    //     return offerRepository.findByTier(tier);
    // }

       
    private void sendToFraudDetection(Transaction transaction) {
        try {
            // Only send REDEMPTIONS to fraud detection, not CLAIMS
            // Claims are just earning points - no fraud risk there
            if (!"REDEMPTION".equals(transaction.getType())) {
                return; // Skip fraud check for CLAIM transactions
            }
           
            // Forward all REDEMPTION transactions to fraud detection.
            // Fraud_MS will apply the configured rules (Option A) and decide risk levels.
           
            // Send to Fraud Detection Service
            FraudTransactionRequest request = new FraudTransactionRequest();
            request.setExternalId(transaction.getExternalId());
            request.setAccountId("ACC-" + transaction.getUserId());
            request.setType(transaction.getType());
            request.setPointsEarned(transaction.getPointsEarned());
            request.setPointsRedeemed(transaction.getPointsRedeemed());
            request.setStore(transaction.getStore());
            request.setDate(transaction.getDate());
            request.setExpiry(transaction.getExpiry());
            request.setNote(transaction.getNote());
            request.setUserId(transaction.getUserId());
           
            fraudDetectionClient.sendTransactionForFraudCheck(request);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Failed to send transaction to fraud detection: " + e.getMessage());
        }
    }
}   
