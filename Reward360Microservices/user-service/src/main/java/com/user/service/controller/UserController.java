// package com.user.service.controller;

// import com.user.service.client.CustomerServiceClient;
// import com.user.service.dto.CustomerProfileDto;
// import com.user.service.model.User;
// import com.user.service.service.UserService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api")
// @RequiredArgsConstructor
// public class UserController {

//     private final UserService userService;
//     private final CustomerServiceClient customerClient;

//     @GetMapping("/users")
//     public ResponseEntity<List<User>> getAllUsers() {
//         return ResponseEntity.ok(userService.findAllUsers());
//     }

//     @PostMapping("/addcustomer")
//     public ResponseEntity<String> addCustomer(@RequestBody CustomerProfileDto profileDto) {
//         // Send data to customer service via Feign Client
//         try {
//           customerClient.createProfile(profileDto);
//             return ResponseEntity.ok("Customer added successfully via Customer Service");
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body("Failed to add customer: " + e.getMessage());
//         }
//     }

//     @GetMapping("/getuserbyid")
//     public ResponseEntity<?> getLoggedInUserId(Authentication authentication) {
//         if (authentication == null || !authentication.isAuthenticated()) {
//             return ResponseEntity.status(401).body("Not authenticated");
//         }

//         String email = authentication.getName(); // In Basic Auth/JWT, name is usually the username/email
//         try {
//             User user = userService.getUserByEmail(email);
//             return ResponseEntity.ok(user.getId());
//         } catch (Exception e) {
//             return ResponseEntity.notFound().build();
//         }
//     }
// }
