package com.cts.repository;
import com.cts.entity.CustomerProfile;
public interface CustomerProfilerepository extends org.springframework.data.jpa.repository.JpaRepository<com.cts.entity.CustomerProfile, Long> {
    CustomerProfile findByUserId(Long userId);
}
