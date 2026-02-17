package com.example.dashboard_backend.dto;

/**
 * DTO for fetching transaction data from CustomerMS
 * Contains only id, type, and note fields from the Transaction table
 */
public record TransactionDTO(Long id, String type, String note) {
}