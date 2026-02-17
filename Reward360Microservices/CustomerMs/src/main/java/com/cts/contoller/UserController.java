package com.cts.contoller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cts.dto.ClaimRequest;
import com.cts.dto.OfferDto;
import com.cts.dto.RedeemRequest;
import com.cts.entity.CustomerProfile;
import com.cts.entity.Redemption;
import com.cts.entity.Transaction;
import com.cts.service.Pointsservice;
import com.cts.dto.RegisterRequest;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/users")
 // Allow requests from any origin (for development purposes)
public class UserController {

    @Autowired
    private Pointsservice pointService;


    @PostMapping("/addcustomer")
    public ResponseEntity<CustomerProfile> registerUser(@RequestBody CustomerProfile registerRequest) {
        
        CustomerProfile createdProfile = pointService.registerUser(registerRequest);
        return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);
    }

    @GetMapping("/Customer/{id}")
public ResponseEntity<CustomerProfile> getCustomer(@PathVariable("id") Long id) { // Added ("id")
    CustomerProfile customerProfile = pointService.getCutomerId(id);
    return ResponseEntity.ok(customerProfile);
}
    @PostMapping("/redeem/offer/{offerId}/user/{userId}")
    public ResponseEntity<Redemption> redeemOffer(@RequestBody RedeemRequest redeemRequest,
            @PathVariable("userId") Long userId
           ) {
        Redemption redemption = pointService.redeemOffer(userId, redeemRequest);
        return new ResponseEntity<>(redemption, HttpStatus.CREATED);
    }

    @PostMapping("/claim/user/{userId}")
    public ResponseEntity<ClaimRequest> claimOffer(@RequestBody ClaimRequest claimRequest,
            @PathVariable("userId") Long userId) {
        ClaimRequest claim = pointService.claimOffer(claimRequest, userId);
        return new ResponseEntity<>(claim, HttpStatus.CREATED);
    }

    @GetMapping("/transactions/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable("userId") Long userId) {
        List<Transaction> transactions = pointService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }

   @GetMapping("/offers/teir/{tier}")
    public ResponseEntity<List<OfferDto>> getOffersByTier(@PathVariable("tier") String tier) {
        // Now calling the service which returns DTOs via Feign
        List<OfferDto> offers = pointService.getOffersByTier(tier);
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/redemptions/user/{userId}")
    public ResponseEntity<List<Redemption>> getRedemptionsByUserId(@PathVariable("userId") Long userId) {
        List<Redemption> redemptions = pointService.getRedemptionsByUserId(userId);
        return ResponseEntity.ok(redemptions);
    }



    // Admin endpoint to view all redemptions in the system
    @GetMapping("/redemptions")
    public ResponseEntity<List<Redemption>> getAllRedemptions() {
        List<Redemption> redemptions = pointService.getAllRedemptions();
        return ResponseEntity.ok(redemptions);
    }

}
