package com.example.dashboard_backend.client;

import com.example.dashboard_backend.dto.TransactionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "CUSTOMERMS", fallback = CustomerTransactionFallback.class)
public interface CustomerTransactionClient {

    @GetMapping("/api/users/transactions/user/{userId}")
    List<TransactionDTO> getTransactionsByUserId(@PathVariable("userId") Long userId);
    
    @GetMapping("/api/users/transactions/getAll")
    List<TransactionDTO> getAllTransactions();
}


