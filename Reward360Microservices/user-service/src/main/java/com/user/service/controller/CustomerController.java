// package com.user.service.controller;

// import com.user.service.dto.CustomerProfileDto;
// import com.user.service.model.CustomerProfile;
// import com.user.service.service.UserService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/customer")
// @RequiredArgsConstructor
// public class CustomerController {

//     private final UserService userService;

//     @GetMapping("/dashboard/{userId}")
//     public ResponseEntity<CustomerProfile> getCustomerProfile(@PathVariable Long userId) {
//         CustomerProfile profile = userService.getCustomerProfile(userId);
//         return ResponseEntity.ok(profile);
//     }

//     @PutMapping("/dashboard/{userId}")
//     public ResponseEntity<CustomerProfile> updateCustomerProfile(@PathVariable Long userId,
//             @RequestBody CustomerProfileDto profileDto) {
//         CustomerProfile updatedProfile = userService.updateCustomerProfile(userId, profileDto);
//         return ResponseEntity.ok(updatedProfile);
//     }
// }
