package com.example.dashboard_backend.client;

import com.example.dashboard_backend.dto.TransactionDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Fallback implementation for CustomerTransactionClient
 * Returns empty data when CustomerMs service is unavailable
 */
@Component
public class CustomerTransactionFallback implements CustomerTransactionClient {

    @Override
    public List<TransactionDTO> getTransactionsByUserId(Long userId) {
        // Return empty list when CustomerMs is down
        System.err.println("CustomerMs service is unavailable. Returning empty transaction list for userId: " + userId);
        return Collections.emptyList();
    }

    @Override
    public List<TransactionDTO> getAllTransactions() {
        // Return empty list when CustomerMs is down
        System.err.println("CustomerMs service is unavailable. Returning empty transaction list.");
        return Collections.emptyList();
    }
}