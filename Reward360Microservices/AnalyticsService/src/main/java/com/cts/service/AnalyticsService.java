package com.cts.service;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.cts.Feign.CustomerServiceClient;
import com.cts.Feign.PromotionServiceClient;
import com.cts.Feign.UserServiceClient;

import com.cts.dto.*;
import com.cts.model.Report;
import com.cts.repository.ReportRepository;
import com.cts.dto.KPIResponse;
@Service
public class AnalyticsService {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);

    private final UserServiceClient userClient;
    private final PromotionServiceClient promotionClient;
    private final CustomerServiceClient redemptionClient;
    private final ReportRepository reportRepo;

    public AnalyticsService(UserServiceClient userClient, 
                            PromotionServiceClient promotionClient,
                            CustomerServiceClient redemptionClient, 
                            ReportRepository reportRepo) {
        this.userClient = userClient;
        this.promotionClient = promotionClient;
        this.redemptionClient = redemptionClient;
        this.reportRepo = reportRepo;
    }

    // KPIs - Using .size() instead of .count()
    public KPIResponse getKPIs() {
        try {
            int users = userClient.getAllUsers().size();
            int offers = promotionClient.getAllOffers().size();
            int redemptions = redemptionClient.getAllRedemptions().size();

            double rate = users > 0 ? (double) redemptions / users * 100 : 0;

            return new KPIResponse((long) users, (long) offers, (long) redemptions, rate);
        } catch (Exception ex) {
            logger.error("Error while fetching KPIs from microservices", ex);
            return new KPIResponse(0L, 0L, 0L, 0.0);
        }
    }

    // Trends - Restored previous logic using Feign Client data
    public TrendResponse getTrend(String metric) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

        return switch (metric.toLowerCase()) {
            case "users" -> {
                Map<YearMonth, Long> usersByMonth = userClient.getAllUsers().stream()
                        .collect(Collectors.groupingBy(
                                u -> YearMonth.from(u.getCreatedAt()),
                                Collectors.counting()
                        ));
                List<YearMonth> sortedUserMonths = usersByMonth.keySet().stream().sorted().toList();
                yield new TrendResponse(
                        sortedUserMonths.stream().map(m -> m.format(formatter)).toList(),
                        sortedUserMonths.stream().map(usersByMonth::get).toList()
                );
            }
            case "offers" -> {
                Map<YearMonth, Long> offersByMonth = promotionClient.getAllOffers().stream()
                        .collect(Collectors.groupingBy(
                                o -> YearMonth.from(o.getStartDate()),
                                Collectors.counting()
                        ));
                List<YearMonth> sortedOfferMonths = offersByMonth.keySet().stream().sorted().toList();
                yield new TrendResponse(
                        sortedOfferMonths.stream().map(m -> m.format(formatter)).toList(),
                        sortedOfferMonths.stream().map(offersByMonth::get).toList()
                );
            }
            case "redemption" -> {
                Map<YearMonth, Long> redemptionsByMonth = redemptionClient.getAllRedemptions().stream()
                        .collect(Collectors.groupingBy(
                                r -> YearMonth.from(r.getDate()),
                                Collectors.counting()
                        ));
                List<YearMonth> sortedRedemptionMonths = redemptionsByMonth.keySet().stream().sorted().toList();
                yield new TrendResponse(
                        sortedRedemptionMonths.stream().map(m -> m.format(formatter)).toList(),
                        sortedRedemptionMonths.stream().map(redemptionsByMonth::get).toList()
                );
            }
            default -> new TrendResponse(List.of(), List.of());
        };
    }

    public ReportResponse generateReport(String metric) {
        Report report = new Report();
        report.setMetric(metric);
        report.setGeneratedAt(LocalDateTime.now());
        reportRepo.save(report);

        String currentDate = LocalDateTime.now().toString();
        String startDate = "N/A";
        String endDate = currentDate;

        return switch (metric.toLowerCase()) {
            case "users" -> {
                int userCount = userClient.getAllUsers().size();
                yield new ReportResponse(metric, startDate, endDate,
                        List.of("Total Users"), List.of(userCount));
            }
            case "offers" -> {
                int offerCount = promotionClient.getAllOffers().size();
                yield new ReportResponse(metric, startDate, endDate,
                        List.of("Total Offers"), List.of(offerCount));
            }
            case "redemption" -> {
                int redemptionCount = redemptionClient.getAllRedemptions().size();
                yield new ReportResponse(metric, startDate, endDate,
                        List.of("Total Redemptions"), List.of(redemptionCount));
            }
            default -> new ReportResponse(metric, startDate, endDate, List.of(), List.of());
        };
    }

    // History methods using Feign Clients
    public List<User> getUsersHistory() {
        return userClient.getAllUsers();
    }

    public List<Promotion> getOffersHistory() {
        return promotionClient.getAllOffers();
    }

    public List<Redemption> getRedemptionsHistory() {
        return redemptionClient.getAllRedemptions();
    }

    public List<Report> getReportsHistory() {
        return reportRepo.findAll();
    }
}