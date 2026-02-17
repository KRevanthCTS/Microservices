package com.cts.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.dto.*;
import com.cts.service.AnalyticsService;
import com.cts.Feign.CustomerServiceClient;
import com.cts.Feign.PromotionServiceClient;
import com.cts.Feign.UserServiceClient;
import com.cts.model.Report;


@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService service;

    // Constructor Injection
    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/kpis")
    public KPIResponse getKPIs() {
        // Logic remains in service, now fetching from Feign Clients
        return service.getKPIs();
    }

    @GetMapping("/trends")
    public TrendResponse getTrends(@RequestParam("metric") String metric) {
        return service.getTrend(metric);
    }

    @GetMapping("/report")
    public ReportResponse getReport(@RequestParam("metric") String metric) {
        return service.generateReport(metric);
    }

    @GetMapping("/reports")
    public List<Report> getReportsHistory() {
        return service.getReportsHistory();
    }

    // ✅ Users history: Now references data fetched via UserServiceClient
    @GetMapping("/users")
    public List<User> getUsersHistory() {
        return service.getUsersHistory();
    }

    // ✅ Offers history: Now references data fetched via PromotionServiceClient
    @GetMapping("/offers")
    public List<Promotion> getOffersHistory() {
        return service.getOffersHistory();
    }

    // ✅ Redemptions history: Now references data fetched via CustomerServiceClient
    @GetMapping("/redemptions")
    public List<Redemption> getRedemptionsHistory() {
        return service.getRedemptionsHistory();
    }
}