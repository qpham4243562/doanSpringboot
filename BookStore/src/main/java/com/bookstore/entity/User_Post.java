package com.bookstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "user_post")
public class User_Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nội dung không được để trống")
    @Column(name = "content")
    private String content;
    @NotNull(message = "Vui lòng chọn một lớp học")
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;
    @NotNull(message = "Vui lòng chọn một môn học")
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectEntity subjectEntity;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Size(max = 10, message = "Số lượng hình ảnh không được vượt quá 10")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_post_id")
    private List<Image> images;

    @Column(name = "likes")
    private int likes;
    @Column(name = "approved")
    private boolean approved = false;
    @OneToMany(mappedBy = "userPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPostLike> userPostLikes;
    @Transient
    private int commentCount;
    @OneToMany(mappedBy = "userPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @OneToMany(mappedBy = "userPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostReport> reports;
    public int getCommentCount() {
        return comments != null ? comments.size() : 0;
    }
    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public List<UserPostLike> getUserPostLikes() {
        return userPostLikes;
    }



}
