package com.example.dashboard_backend.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dashboard_backend.entity.Transaction;
import com.example.dashboard_backend.repository.TransactionRepository;
import com.example.dashboard_backend.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public Transaction create(Transaction tx) {
        if (tx.getCreatedAt() == null) {
            tx.setCreatedAt(Instant.now());
        }
        Transaction saved = transactionRepository.save(tx);
        applyFraudRules(saved);
        return transactionRepository.save(saved);
    }

    /**
     * Apply fraud detection rules to a transaction. Rule 1: High value
     * redemptions (>10000 points) Rule 2: Velocity check (multiple redemptions
     * in 10 minutes) Rule 3: Unusual account activity (>20 transactions in 1
     * hour)
     */
    private void applyFraudRules(Transaction tx) {
        // Rule 1: Check for high value redemptions (potential fraud)
        if (tx.getPointsRedeemed() != null && tx.getPointsRedeemed() > 10000) {
            tx.setRiskLevel("HIGH");
            tx.setStatus("REVIEW");
            tx.setDescription("Flagged: High value redemption (>10000 points)");
            return;
        }

        // Rule 2: Check for velocity (multiple redemptions in short time)
        if (tx.getType() != null && tx.getType().equals("REDEMPTION")) {
            if (tx.getUserId() != null) {
                Instant tenMinutesAgo = Instant.now().minusSeconds(600);
                List<Transaction> recentRedemptions = transactionRepository
                        .findByUserAndTypeAndCreatedAtAfter(tx.getUserId(), "REDEMPTION", tenMinutesAgo);

                int redemptionCount = recentRedemptions.size() + 1; // Include current transaction

                if (redemptionCount >= 10) {
                    tx.setRiskLevel("CRITICAL");
                    tx.setStatus("REVIEW");
                    tx.setDescription("Flagged: Excessive redemptions (10+ in 10 min)");
                    return;
                } else if (redemptionCount >= 5) {
                    tx.setRiskLevel("MEDIUM");
                    tx.setStatus("REVIEW");
                    tx.setDescription("Flagged: Multiple redemptions (5-9 in 10 min)");
                    return;
                } else if (redemptionCount >= 3) {
                    tx.setRiskLevel("LOW");
                    tx.setStatus("REVIEW");
                    tx.setDescription("Flagged: Multiple redemptions (3-4 in 10 min)");
                    return;
                }
            }
        }

        // Rule 3: Check for unusual account activity
        if (tx.getAccountId() != null) {
            Instant oneHourAgo = Instant.now().minusSeconds(3600);
            List<Transaction> recentTransactions = transactionRepository
                    .findByAccountIdAndCreatedAtAfter(tx.getAccountId(), oneHourAgo);

            if (recentTransactions.size() > 20) {
                tx.setRiskLevel("MEDIUM");
                tx.setStatus("REVIEW");
                tx.setDescription("Flagged: Unusual account activity (>20 transactions in 1 hour)");
                return;
            }
        }

        // Default: No fraud detected
        if (tx.getRiskLevel() == null) {
            tx.setRiskLevel("LOW");
        }
        if (tx.getStatus() == null || tx.getStatus().isEmpty()) {
            tx.setStatus("CLEARED");
        }
    }
}
