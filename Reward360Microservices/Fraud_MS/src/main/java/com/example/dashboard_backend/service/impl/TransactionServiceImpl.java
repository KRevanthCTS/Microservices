package com.example.dashboard_backend.service.impl;
 
import java.time.Instant;
import java.util.List;
import java.util.Optional;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import com.example.dashboard_backend.entity.Transaction;
import com.example.dashboard_backend.repository.TransactionRepository;
import com.example.dashboard_backend.service.TransactionService;
 
@Service
public class TransactionServiceImpl implements TransactionService {
   
    private final TransactionRepository transactionRepository;
   
    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
   
    @Override
    public List<Transaction> list(String accountId, String riskLevel, String status, String paymentMethod,
                                   Instant from, Instant to, String q) {
        return transactionRepository.findFiltered(accountId, riskLevel, status, paymentMethod, from, to, q);
    }
   
    @Override
    public Optional<Transaction> get(Long id) {
        return transactionRepository.findById(id);
    }
   
    @Override
    @Transactional
    public Transaction create(Transaction tx) {
        if (tx.getCreatedAt() == null) tx.setCreatedAt(Instant.now());
        Transaction saved = transactionRepository.save(tx);
        applyFraudRules(saved);
        return transactionRepository.save(saved);
    }
   
    @Override
    @Transactional
    public Optional<Transaction> updateStatus(Long id, String newStatus) {
        Optional<Transaction> txOpt = transactionRepository.findById(id);
        if (txOpt.isPresent()) {
            Transaction tx = txOpt.get();
            tx.setStatus(newStatus);
            return Optional.of(transactionRepository.save(tx));
        }
        return Optional.empty();
    }
   
    @Override
    @Transactional
    public Transaction markForReview(Long transactionId, String userId, String username, String reason) {
        Transaction tx = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
        tx.setStatus("REVIEW");
        tx.setRiskLevel("HIGH");
        return transactionRepository.save(tx);
    }
   
    @Override
    @Transactional
    public Transaction blockTransaction(Long transactionId, String userId, String username, String reason) {
        Transaction tx = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
        tx.setStatus("BLOCKED");
        tx.setRiskLevel("CRITICAL");
        return transactionRepository.save(tx);
    }
   
    @Override
    @Transactional
    public Transaction clearTransaction(Long transactionId, String userId, String username, String reason) {
        Transaction tx = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
        tx.setStatus("APPROVED");
        tx.setRiskLevel("LOW");
        return transactionRepository.save(tx);
    }
   
    @Override
    @Transactional
    public void bulkMarkForReview(Long[] transactionIds, String userId, String username, String reason) {
        for (Long id : transactionIds) {
            markForReview(id, userId, username, reason);
        }
    }
   
    @Override
    @Transactional
    public void bulkBlockTransactions(Long[] transactionIds, String userId, String username, String reason) {
        for (Long id : transactionIds) {
            blockTransaction(id, userId, username, reason);
        }
    }
   
    @Override
    @Transactional
    public void processTransaction(Long transactionId) {
        Transaction tx = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
       
        // Apply fraud detection rules
        System.out.println("Processing transaction " + tx.getId() + " - Points Redeemed: " + tx.getPointsRedeemed());
        applyFraudRules(tx);
       
        transactionRepository.save(tx);
    }
   
    /**
     * Apply fraud detection rules to a transaction
     */
    private void applyFraudRules(Transaction tx) {
        // Rule 1: Check for high value redemptions (potential fraud)
        System.out.println("Applying fraud rules to transaction " + tx.getId() + " - Points Redeemed: " + tx.getPointsRedeemed());
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
                System.out.println("Recent redemptions for user " + tx.getUserId() + ": " + redemptionCount);
               
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
       
        // Default: No fraud detected, clear the transaction
        if (tx.getRiskLevel() == null) {
            tx.setRiskLevel("LOW");
        }
        if (tx.getStatus() == null || tx.getStatus().isEmpty()) {
            tx.setStatus("CLEARED");
        }
        System.out.println("Transaction " + tx.getId() + " - Points Redeemed: " + tx.getPointsRedeemed()+" - Risk Level: " + tx.getRiskLevel());
       
    }
   
    @Override
    @Transactional
    public void reprocessAllTransactions() {
        List<Transaction> allTransactions = transactionRepository.findAll();
        for (Transaction tx : allTransactions) {
            applyFraudRules(tx);
            transactionRepository.save(tx);
        }
    }
}
 
 
 
 