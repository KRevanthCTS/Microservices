package com.user.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.user.service.model.CustomerProfile;
import com.user.service.model.User;
import java.util.Optional;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {
    Optional<CustomerProfile> findByUser(User user);
}
