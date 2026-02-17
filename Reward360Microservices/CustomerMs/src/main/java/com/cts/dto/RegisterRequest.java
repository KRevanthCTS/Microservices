package com.cts.dto;

import jakarta.validation.constraints.*;
public class RegisterRequest {
        @NotBlank String name;
        @Email String email;
        @Pattern(regexp="^[0-9]{10}$", message = "Phone must be 10 digits") String phone;
        @Size(min=8, message="Password must be at least 8 characters") String password;
        @NotBlank String role;
        String preferences;
        String communication;

        public String getName() {
                return name;
        }

        public String getEmail() {
                return email;
        }

        public String getPhone() {
                return phone;
        }

        public String getPassword() {
                return password;
        }

        public String getRole() {
                return role;
        }

        public String getPreferences() {
                return preferences;
        }

        public String getCommunication() {
                return communication;
        }
        public RegisterRequest(String name, String email, String phone, String password, String role, String preferences, String communication) {
                this.name = name;
                this.email = email;
                this.phone = phone;
                this.password = password;
                this.role = role;
                this.preferences = preferences;
                this.communication = communication;
        }
         public RegisterRequest() {
        }

}