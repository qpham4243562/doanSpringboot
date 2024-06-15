package com.bookstore.repository;

import com.bookstore.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUserPostId(Long userPostId);
    int countByUserPostId(Long userPostId);
}
