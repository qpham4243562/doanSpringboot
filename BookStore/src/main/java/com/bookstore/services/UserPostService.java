package com.bookstore.services;

import com.bookstore.entity.Image;
import com.bookstore.entity.User;
import com.bookstore.entity.User_Post;
import com.bookstore.repository.IUserPostRepository;
import com.bookstore.repository.ImageRepository;
import com.bookstore.repository.UserPostLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.util.Date;
import java.util.List;

@Service
public class UserPostService {

    @Autowired
    private IUserPostRepository userPostRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserPostLikeRepository userPostLikeRepository;
    @Autowired
    private CommentService commentService;

    public User_Post createUserPost(User_Post userPost) {
        userPost.setCreatedAt(new Date());
        userPost.setUpdatedAt(new Date());
        return userPostRepository.save(userPost);
    }

    public List<User_Post> getAllUserPosts() {
        return userPostRepository.findAll();
    }

    public User_Post getUserPostById(Long id) {
        return userPostRepository.findById(id).orElse(null);
    }

    public User_Post updateUserPost(User_Post userPost) {
        userPost.setUpdatedAt(new Date());
        return userPostRepository.save(userPost);
    }
    public int getCommentCountByPostId(Long postId) {
        return commentService.countCommentsByUserPostId(postId);
    }
    public void deleteUserPost(Long id) {
        userPostRepository.deleteById(id);
    }
    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElse(null);
    }
    public boolean hasUserLikedPost(User user, User_Post userPost) {
        return userPostLikeRepository.findByUserAndUserPost(user, userPost) != null;
    }
    public List<User_Post> getAllUserPostsWithUser() {
        return userPostRepository.findAllWithUser();
    }
    public List<User_Post> getUserPostsByClassAndSubject(Long classId, Long subjectId) {
        return userPostRepository.findByClassEntityIdAndSubjectEntityId(classId, subjectId);
    }

    public List<User_Post> getUserPostsByClass(Long classId) {
        return userPostRepository.findByClassEntityId(classId);
    }

    public List<User_Post> getUserPostsBySubject(Long subjectId) {
        return userPostRepository.findBySubjectEntityId(subjectId);
    }
    public List<User_Post> getAllUnapprovedPosts() {
        return userPostRepository.findByApproved(false);
    }

    public User_Post approvePost(Long id) {
        User_Post userPost = userPostRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        userPost.setApproved(true);
        return userPostRepository.save(userPost);
    }
    public List<User_Post> getAllApprovedUserPosts() {
        return userPostRepository.findByApproved(true);
    }
}