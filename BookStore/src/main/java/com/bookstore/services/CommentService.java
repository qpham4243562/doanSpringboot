package com.bookstore.services;

import com.bookstore.entity.Comment;
import com.bookstore.entity.User;
import com.bookstore.repository.CommentLikeRepository;
import com.bookstore.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    public List<Comment> getCommentsByUserPostId(Long userPostId) {
        return commentRepository.findByUserPostId(userPostId);
    }

    public void addComment(Comment comment) {
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (comment.getUser().equals(user)) {
            commentLikeRepository.deleteByCommentId(commentId);

            commentRepository.delete(comment);
        } else {
            throw new SecurityException("You are not allowed to delete this comment");
        }
    }

    public int countCommentsByUserPostId(Long postId) {
        return commentRepository.countByUserPostId(postId);
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }
}
