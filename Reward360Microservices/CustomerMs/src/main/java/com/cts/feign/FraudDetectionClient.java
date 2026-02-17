package com.cts.feign;
 
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
 
import com.cts.dto.FraudTransactionRequest;
 
@FeignClient(name = "FRAUD-DETECTION-SERVICE")
public interface FraudDetectionClient {
   
    @PostMapping("/api/v1/transactions")
    void sendTransactionForFraudCheck(@RequestBody FraudTransactionRequest request);
}
 
 

 