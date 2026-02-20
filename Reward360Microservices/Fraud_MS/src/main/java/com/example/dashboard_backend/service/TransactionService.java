package com.example.dashboard_backend.service;

import com.example.dashboard_backend.entity.Transaction;

/**
 * Transaction Service - Handles transaction creation with fraud detection
 */
public interface TransactionService {

    Transaction create(Transaction tx);
}
