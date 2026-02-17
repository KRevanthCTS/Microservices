
package com.cts.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.Redemption;


public interface RedemptionRepository extends JpaRepository<Redemption, Long> {
    List<Redemption> findByUserId(Long userId);
    
    // List<Redemption> findByUserId(Long userId);
    // or with sorting:
    List<Redemption> findByUserIdOrderByDateDesc(Long userId);
}
