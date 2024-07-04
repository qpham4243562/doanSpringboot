package com.bookstore.services;

import com.bookstore.entity.Comment;
import com.bookstore.entity.CommentLike;
import com.bookstore.entity.User;
import com.bookstore.repository.CommentLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentLikeService {
    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Transactional
    public void likeComment(User user, Comment comment) {
        if (!commentLikeRepository.existsByUserAndComment(user, comment)) {
            CommentLike like = new CommentLike();
            like.setUser(user);
            like.setComment(comment);
            like.setCreatedAt(new Date());
            commentLikeRepository.save(like);
        }
    }

    @Transactional
    public void unlikeComment(User user, Comment comment) {
        commentLikeRepository.deleteByUserAndComment(user, comment);
    }

    public int getLikeCount(Long commentId) {
        return commentLikeRepository.countByCommentId(commentId);
    }
    public boolean isLikedByUser(Long commentId, Long userId) {
        return commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
    }
}