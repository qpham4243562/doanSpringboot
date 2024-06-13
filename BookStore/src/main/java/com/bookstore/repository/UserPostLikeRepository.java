package com.bookstore.repository;

import com.bookstore.entity.User;
import com.bookstore.entity.UserPostLike;
import com.bookstore.entity.User_Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPostLikeRepository extends JpaRepository<UserPostLike, Long> {
    UserPostLike findByUserAndUserPost(User user, User_Post userPost);
}