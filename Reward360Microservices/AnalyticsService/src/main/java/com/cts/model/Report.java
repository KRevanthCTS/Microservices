package com.cts.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String metric;
    private LocalDateTime generatedAt;

    public Report() {
    }
    public Report(String metric, LocalDateTime generatedAt) {
        this.metric = metric;
        this.generatedAt = generatedAt;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMetric() {
        return metric;
    }

    
    public void setMetric(String metric) {
        this.metric = metric;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", metric='" + metric + '\'' +
                ", generatedAt=" + generatedAt +
                '}';
    }
    
}
