// package com.cts.entity;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.OneToOne;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import jakarta.persistence.CascadeType;
// import com.fasterxml.jackson.annotation.JsonIgnore;

// public class User {
    
//     private Long id;
//     private String name;
//     @Column(unique = true)
//     private String email;
//     private String phone;
//     private String password;
//    String role; // ADMIN or USER

    
//     // Getters and Setters
//     public Long getId() {   
//         return id;
//     }   
//     public void setId(Long id) {
//         this.id = id;
//     }
//     public String getName() {
//         return name;
//     }
//     public void setName(String name) {
//         this.name = name;
//     }
//     public String getEmail() {
//         return email;
//     }
//     public void setEmail(String email) {
//         this.email = email;
//     }
//     public String getPhone() {
//         return phone;
//     }
//     public void setPhone(String phone) {
//         this.phone = phone;
//     }
//     public String getPassword() {
//         return password;
//     }
//     public void setPassword(String password) {
//         this.password = password;
//     }
//     public String getRole() {
//         return role;
//     }
//     public void setRole(String role) {
//         this.role = role;
//     }
//     public CustomerProfile getProfile() {
//         return profile;
//     }
//     public void setProfile(CustomerProfile profile) {
//         this.profile = profile;
//     }
//        public User() {
//        }
//        public User(String name, String email, String phone, String password, String role) {
//            this.name = name;
//            this.email = email;
//            this.phone = phone;
//            this.password = password;
//            this.role = role;
//        }
// }