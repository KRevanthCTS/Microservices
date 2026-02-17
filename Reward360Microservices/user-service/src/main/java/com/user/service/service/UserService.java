package com.user.service.service;

import com.user.service.client.CustomerServiceClient;
import com.user.service.dto.CustomerProfileDto;
import com.user.service.dto.LoginDto;
import com.user.service.dto.UserDto;
import com.user.service.model.CustomerProfile;
import com.user.service.model.Role;
import com.user.service.model.User;
import com.user.service.repository.CustomerProfileRepository;
import com.user.service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerProfileRepository profileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomerServiceClient customerServiceClient;

  

    public User registerUser(UserDto userDto) {
        // 1. Save Identity in Auth DB

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole().equalsIgnoreCase("ADMIN") ? Role.ADMIN : Role.USER);
        user.setCreatedAt(java.time.LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // 2. Map data to the DTO
        CustomerProfileDto profileDto = new CustomerProfileDto();
        profileDto.setUserId(savedUser.getId());
        profileDto.setCustomerName(savedUser.getName());
        profileDto.setEmail(savedUser.getEmail());
        profileDto.setPhone(savedUser.getPhone());
        profileDto.setLoyaltyTier("Bronze"); // Default tier
        profileDto.setPointsBalance(1000);
        profileDto.setCommunication("Email"); // Default preference
        profileDto.setPreferences("Fashion");
        profileDto.setLifetimePoints(1000);
        // 3. Send to Customer Service
        customerServiceClient.createProfile(profileDto);

        return savedUser;
    }


    public User loginUser(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return user;
    }
//
//    public CustomerProfile getCustomerProfile(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        return user.getProfile();
//    }
//
//    public CustomerProfile updateCustomerProfile(Long userId, CustomerProfileDto profileDto) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        CustomerProfile profile = user.getProfile();
//        if (profile == null) {
//            profile = new CustomerProfile();
//            profile.setUser(user);
//        }
//
//        // Update fields if provided
//        if (profileDto.getCommunication() != null)
//            profile.setCommunication(profileDto.getCommunication());
//        if (profileDto.getPreferences() != null)
//            profile.setPreferences(profileDto.getPreferences());
//        // Loyalty points updates usually come from transactions, but allowing manual
//        // update here for demo
//        // Assuming points update logic is handled elsewhere or passed here.
//        // For simplicity, just update preferences/communication as requested for
//        // "profile section".
//
//        return profileRepository.save(profile);
//    }

    public java.util.List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
