package com.example.dashboard_backend.controller;

import com.example.dashboard_backend.client.CustomerTransactionClient;
import com.example.dashboard_backend.dto.TransactionDTO;
import com.example.dashboard_backend.entity.Transaction;
import com.example.dashboard_backend.repository.TransactionRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionRepository txRepo;
    private final CustomerTransactionClient customerTransactionClient;
    private final com.example.dashboard_backend.service.TransactionService transactionService;

    public TransactionController(TransactionRepository txRepo,
            CustomerTransactionClient customerTransactionClient,
            com.example.dashboard_backend.service.TransactionService transactionService) {
        this.txRepo = txRepo;
        this.customerTransactionClient = customerTransactionClient;
        this.transactionService = transactionService;
    }

    // ===== List with filters (Points-Based) =====
    @GetMapping
    public List<Transaction> list(
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String riskLevel, // LOW/MEDIUM/HIGH/CRITICAL
            @RequestParam(required = false) String status, // CLEARED/REVIEW/BLOCKED
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) Integer minPoints,
            @RequestParam(required = false) Integer maxPoints
    ) {
        String risk = riskLevel != null ? riskLevel.toUpperCase() : null;
        String st = status != null ? status.toUpperCase() : null;

        if (allNull(accountId, risk, st, from, to, minPoints, maxPoints)) {
            return txRepo.findTop100ByOrderByCreatedAtDesc();
        }

        return txRepo.findAll().stream()
                .filter(tx -> accountId == null || accountId.equals(tx.getAccountId()))
                .filter(tx -> risk == null || risk.equals(tx.getRiskLevel()))
                .filter(tx -> st == null || st.equals(tx.getStatus()))
                .filter(tx -> from == null || tx.getCreatedAt().isAfter(from))
                .filter(tx -> to == null || tx.getCreatedAt().isBefore(to))
                .filter(tx -> minPoints == null || (tx.getPointsRedeemed() != null && tx.getPointsRedeemed() >= minPoints))
                .filter(tx -> maxPoints == null || (tx.getPointsRedeemed() != null && tx.getPointsRedeemed() <= maxPoints))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(100)
                .toList();
    }

    private boolean allNull(Object... vals) {
        for (Object v : vals) {
            if (v != null) {
                return false;
            }
        }
        return true;
    }

    // ===== Get one =====
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> get(@PathVariable Long id) {
        Optional<Transaction> opt = txRepo.findById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ===== Create (Points Transaction) =====
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Transaction tx) {
        try {
            Transaction savedTx = transactionService.create(tx);
            return ResponseEntity.ok(savedTx);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating transaction: " + e.getMessage());
        }
    }

    // ===== Quick actions =====
    @PostMapping("/{id}/review")
    public ResponseEntity<Transaction> markReview(@PathVariable Long id) {
        return updateStatus(id, "REVIEW");
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<Transaction> markBlocked(@PathVariable Long id) {
        return updateStatus(id, "BLOCKED");
    }

    @PostMapping("/{id}/clear")
    public ResponseEntity<Transaction> markCleared(@PathVariable Long id) {
        return updateStatus(id, "CLEARED");
    }

    private ResponseEntity<Transaction> updateStatus(Long id, String newStatus) {
        Optional<Transaction> opt = txRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Transaction tx = opt.get();
        tx.setStatus(newStatus.toUpperCase());
        tx.setUpdatedAt(Instant.now());
        return ResponseEntity.ok(txRepo.save(tx));
    }

    // ===== Export CSV (applies the same filters) =====
    @GetMapping(value = "/export", produces = "text/csv")
    public void exportCsv(
            HttpServletResponse response,
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) Integer minPoints,
            @RequestParam(required = false) Integer maxPoints
    ) throws Exception {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=transactions.csv");
        response.setContentType("text/csv");
        List<Transaction> data = list(accountId, riskLevel, status, from, to, minPoints, maxPoints);

        try (PrintWriter writer = response.getWriter()) {
            writer.println("id,externalId,pointsEarned,pointsRedeemed,date,accountId,riskLevel,status,createdAt,updatedAt");
            for (Transaction t : data) {
                writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        nullSafe(t.getId()),
                        csv(t.getExternalId()),
                        csv(t.getPointsEarned()),
                        csv(t.getPointsRedeemed()),
                        csv(t.getDate()),
                        csv(t.getAccountId()),
                        csv(t.getRiskLevel()),
                        csv(t.getStatus()),
                        csv(t.getCreatedAt()),
                        csv(t.getUpdatedAt())
                );
            }
        }
    }

    private Object nullSafe(Object o) {
        return o == null ? "" : o;
    }

    private String csv(Object o) {
        if (o == null) {
            return "";
        }
        String s = o.toString();
        // Escape quotes and wrap when needed
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    /**
     * Get transaction data (id, type, note) from CustomerMs for a specific user
     * This endpoint uses Feign client to fetch transactions from CustomerMS
     */
    @GetMapping("/customer/{userId}/transactions")
    public ResponseEntity<List<TransactionDTO>> getCustomerTransactionsByUserId(@PathVariable Long userId) {
        List<TransactionDTO> transactions = customerTransactionClient.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get all transaction data (id, type, note) from CustomerMs This endpoint
     * uses Feign client to fetch all transactions from CustomerMS
     */
    @GetMapping("/customer/transactions/all")
    public ResponseEntity<List<TransactionDTO>> getAllCustomerTransactions() {
        List<TransactionDTO> transactions = customerTransactionClient.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
}
