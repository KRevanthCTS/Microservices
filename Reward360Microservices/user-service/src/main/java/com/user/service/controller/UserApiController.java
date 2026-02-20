package com.user.service.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.user.service.model.User;
import com.user.service.service.UserService;
import com.user.service.util.JwtUtil;

import io.jsonwebtoken.Claims;

@RestController
public class UserApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private com.user.service.client.PromotionServiceClient promotionClient;

    @Autowired
    private com.user.service.client.CustomerServiceClient customerClient;

    @Autowired
    private com.user.service.repository.CustomerProfileRepository customerProfileRepository;

    @Autowired
    private com.user.service.repository.UserRepository userRepository;

    private Optional<String> emailFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        String token = authHeader.substring(7);
        try {
            Claims claims = jwtUtil.parseClaims(token);
            return Optional.ofNullable(claims.getSubject());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Optional<String> emailOpt = emailFromAuthHeader(authHeader);
        if (emailOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Missing or invalid token");
        }
        User user = userService.getUserByEmail(emailOpt.get());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());

        Map<String, Object> profileMap = new HashMap<>();
        try {
            com.user.service.dto.CustomerProfileDto remote = customerClient.getCustomer(user.getId());
            if (remote != null) {
                profileMap.put("loyaltyTier", remote.getLoyaltyTier());
                profileMap.put("pointsBalance", remote.getPointsBalance());
                profileMap.put("lifetimePoints", remote.getLifetimePoints());
                profileMap.put("preferences", remote.getPreferences());
                profileMap.put("communication", remote.getCommunication());
            } else {
                profileMap.put("loyaltyTier", "Bronze");
                profileMap.put("pointsBalance", 0);
                profileMap.put("lifetimePoints", 0);
                profileMap.put("preferences", "");
                profileMap.put("communication", "Email");
            }
        } catch (Exception e) {
            // fallback to local profile if remote call fails
            var profOpt = customerProfileRepository.findByUser(user);
            if (profOpt.isPresent()) {
                var prof = profOpt.get();
                profileMap.put("loyaltyTier", prof.getLoyaltyTier());
                profileMap.put("pointsBalance", prof.getPointsBalance());
                profileMap.put("lifetimePoints", prof.getLifetimePoints());
                profileMap.put("preferences", prof.getPreferences());
                profileMap.put("communication", prof.getCommunication());
            } else {
                profileMap.put("loyaltyTier", "Bronze");
                profileMap.put("pointsBalance", 0);
                profileMap.put("lifetimePoints", 0);
                profileMap.put("preferences", "");
                profileMap.put("communication", "Email");
            }
        }
        userInfo.put("profile", profileMap);
        userInfo.put("role", user.getRole() != null ? user.getRole().name() : "USER");
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/user/transactions")
    public ResponseEntity<?> transactions(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Optional<String> emailOpt = emailFromAuthHeader(authHeader);
        if (emailOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Missing or invalid token");
        }
        User user = userService.getUserByEmail(emailOpt.get());
        Long userId = user.getId();
        try {
            List<Map<String, Object>> tx = customerClient.getTransactions(userId);
            return ResponseEntity.ok(tx);
        } catch (Exception e) {
            // fallback to empty list on error
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/user/offers/")
    public ResponseEntity<?> offersAll() {
        try {
            // Call promotions microservice for the canonical list
            java.util.List<com.user.service.dto.PromotionDto> promotions = promotionClient.getAllPromotions();
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/user/offers/my-tier")
    public ResponseEntity<?> offersForTier(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Optional<String> emailOpt = emailFromAuthHeader(authHeader);
        String tier = "Bronze";
        if (emailOpt.isPresent()) {
            try {
                User user = userService.getUserByEmail(emailOpt.get());
                tier = user.getRole() != null && user.getRole().name().equals("ADMIN") ? "Platinum" : "Bronze";
            } catch (Exception ignored) {
            }
        }
        try {
            java.util.List<java.util.Map<String, Object>> offers = customerClient.getOffersByTier(tier);
            return ResponseEntity.ok(offers);
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/user/redemptions")
    public ResponseEntity<?> redemptions(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Optional<String> emailOpt = emailFromAuthHeader(authHeader);
        if (emailOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Missing or invalid token");
        }
        User user = userService.getUserByEmail(emailOpt.get());
        try {
            java.util.List<java.util.Map<String, Object>> red = customerClient.getRedemptions(user.getId());
            return ResponseEntity.ok(red);
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @PostMapping("/user/claim")
    public ResponseEntity<?> claimPoints(@RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> payload) {
        // payload: activityCode, points, note
        Optional<String> emailOpt = emailFromAuthHeader(authHeader);
        if (emailOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid token"));
        }
        User user = userService.getUserByEmail(emailOpt.get());
        Long userId = user.getId();
        try {
            Object resp = customerClient.claimPoints(payload, userId);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "claim failed"));
        }
    }

    @PostMapping("/user/redeem")
    public ResponseEntity<?> redeemOffer(@RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> payload) {
        Object offerIdObj = payload.get("offerId");
        if (offerIdObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "missing offerId"));
        }
        Long offerId = Long.valueOf(offerIdObj.toString());
        Optional<String> emailOpt = emailFromAuthHeader(authHeader);
        if (emailOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid token"));
        }
        User user = userService.getUserByEmail(emailOpt.get());
        Long userId = user.getId();
        try {
            Object resp = customerClient.redeemOffer(payload, userId, offerId);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "redeem failed"));
        }
    }

    @PutMapping("/user/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> payload) {
        Optional<String> emailOpt = emailFromAuthHeader(authHeader);
        if (emailOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid token"));
        }
        User user = userService.getUserByEmail(emailOpt.get());
        // Update basic profile fields on User
        try {
            Object name = payload.get("name");
            Object phone = payload.get("phone");
            if (name != null) {
                user.setName(name.toString());
            }
            if (phone != null) {
                user.setPhone(phone.toString());
            }
            userRepository.save(user);

            // Build DTO and send to CustomerMs to persist canonical customer profile
            com.user.service.dto.CustomerProfileDto dto = new com.user.service.dto.CustomerProfileDto();
            dto.setUserId(user.getId());
            dto.setCustomerName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            Object prefs = payload.get("preferences");
            Object comm = payload.get("communication");
            if (prefs != null) {
                dto.setPreferences(prefs.toString());
            }
            if (comm != null) {
                dto.setCommunication(comm.toString());
            }

            try {
                customerClient.updateProfile(dto, user.getId());
            } catch (Exception e) {
                // best-effort: if remote fails, mirror locally
                var profOpt = customerProfileRepository.findByUser(user);
                com.user.service.model.CustomerProfile prof;
                if (profOpt.isPresent()) {
                    prof = profOpt.get();
                } else {
                    prof = new com.user.service.model.CustomerProfile();
                    prof.setUser(user);
                }
                if (prefs != null) {
                    prof.setPreferences(prefs.toString());
                }
                if (comm != null) {
                    prof.setCommunication(comm.toString());
                }
                customerProfileRepository.save(prof);
            }

            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "update failed"));
        }
    }
}
