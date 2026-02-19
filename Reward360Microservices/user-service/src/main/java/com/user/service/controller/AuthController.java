package com.user.service.controller;

import com.user.service.dto.LoginDto;
import com.user.service.dto.UserDto;
import com.user.service.model.User;
import com.user.service.service.UserService;
import com.user.service.util.JwtUtil;
import io.jsonwebtoken.Claims;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {
            userService.registerUser(userDto);
            return ResponseEntity.ok(userDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        try {
            User user = userService.loginUser(loginDto);
            // generate JWT token with basic claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("role", user.getRole() != null ? user.getRole().name() : "USER");
            claims.put("name", user.getName());

            String token = jwtUtil.generateToken(claims, user.getEmail());

            Map<String, Object> resp = new HashMap<>();
            resp.put("token", token);
            resp.put("role", user.getRole() != null ? user.getRole().name() : "USER");
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getId());
            userInfo.put("name", user.getName());
            userInfo.put("email", user.getEmail());
            resp.put("user", userInfo);

            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7);
            Claims claims = jwtUtil.parseClaims(token);
            String email = claims.getSubject();
            if (email == null) {
                return ResponseEntity.status(401).body("Invalid token");
            }
            User user = userService.getUserByEmail(email);
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getId());
            userInfo.put("name", user.getName());
            userInfo.put("email", user.getEmail());
            userInfo.put("role", user.getRole() != null ? user.getRole().name() : "USER");
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token or unable to parse");
        }
    }

    @GetMapping("/Users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }
}
