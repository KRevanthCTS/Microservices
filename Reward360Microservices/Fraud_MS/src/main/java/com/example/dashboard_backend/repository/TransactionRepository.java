package com.example.dashboard_backend.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.dashboard_backend.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Recent transactions (used by controller list endpoint)
    List<Transaction> findTop100ByOrderByCreatedAtDesc();

    // For fraud detection - find recent transactions by account
    List<Transaction> findByAccountIdAndCreatedAtAfter(String accountId, Instant after);

    // For fraud rules - find recent redemptions by user and type
    @Query("""
           SELECT t FROM Transaction t
           WHERE t.userId = :userId
             AND t.type = :type
             AND t.createdAt >= :after
           ORDER BY t.createdAt DESC
           """)
    List<Transaction> findByUserAndTypeAndCreatedAtAfter(@Param("userId") Long userId,
            @Param("type") String type,
            @Param("after") Instant after);
}
