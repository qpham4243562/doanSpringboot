package com.bookstore.services;

import com.bookstore.entity.Comment;
import com.bookstore.entity.User;
import com.bookstore.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getCommentsByUserPostId(Long userPostId) {
        return commentRepository.findByUserPostId(userPostId);
    }

    public void addComment(Comment comment) {
        commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment != null && comment.getUser().getId().equals(user.getId())) {
            commentRepository.delete(comment);
        } else {
            throw new RuntimeException("You are not authorized to delete this comment.");
        }
    }

    public int countCommentsByUserPostId(Long postId) {
        return commentRepository.countByUserPostId(postId);
    }
}