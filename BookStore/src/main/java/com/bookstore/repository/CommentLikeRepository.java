package com.bookstore.repository;

import com.bookstore.entity.Comment;
import com.bookstore.entity.CommentLike;
import com.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByUserAndComment(User user, Comment comment);
    void deleteByUserAndComment(User user, Comment comment);
    int countByCommentId(Long commentId);
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    void deleteByCommentId(Long commentId);
}