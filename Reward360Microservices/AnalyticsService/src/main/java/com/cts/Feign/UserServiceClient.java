// UserServiceClient.java
package com.cts.Feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import com.cts.dto.User;
// UserServiceClient.java
@FeignClient(name = "user-service") // Matches Gateway lb://user-service
public interface UserServiceClient {
    @GetMapping("/auth/Users")
    List<User> getAllUsers();
}

