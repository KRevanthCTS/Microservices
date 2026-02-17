package com.cts.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cts.model.Report;

import org.springframework.stereotype.Repository;
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> { }
