package com.bookstore.services;

import com.bookstore.entity.PostReport;
import com.bookstore.entity.User;
import com.bookstore.entity.User_Post;
import com.bookstore.repository.PostReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PostReportService {

    @Autowired
    private PostReportRepository postReportRepository;

    public PostReport createReport(User_Post userPost, User reporter, String reason) {
        PostReport report = new PostReport();
        report.setUserPost(userPost);
        report.setReporter(reporter);
        report.setReason(reason);
        report.setCreatedAt(new Date());
        return postReportRepository.save(report);
    }

    public List<PostReport> getPendingReports() {
        return postReportRepository.findByStatus(PostReport.ReportStatus.PENDING);
    }

    public PostReport getReportById(Long reportId) {
        return postReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public void resolveReport(Long reportId) {
        PostReport report = getReportById(reportId);
        report.setStatus(PostReport.ReportStatus.RESOLVED);
        postReportRepository.save(report);
    }

    public void dismissReport(Long reportId) {
        PostReport report = getReportById(reportId);
        report.setStatus(PostReport.ReportStatus.DISMISSED);
        postReportRepository.save(report);
    }
}