package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "post_report")
public class PostReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_post_id", nullable = false)
    private User_Post userPost;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING;

    public enum ReportStatus {
        PENDING, RESOLVED, DISMISSED
    }
}