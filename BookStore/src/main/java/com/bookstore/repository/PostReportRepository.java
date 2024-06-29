package com.bookstore.repository;

import com.bookstore.entity.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    List<PostReport> findByStatus(PostReport.ReportStatus status);
}