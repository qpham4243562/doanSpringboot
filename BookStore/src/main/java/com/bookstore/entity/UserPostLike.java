package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_post_like")
public class UserPostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "user_post_id")
    private User_Post userPost;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User_Post getUserPost() {
        return userPost;
    }

    public void setUserPost(User_Post userPost) {
        this.userPost = userPost;
    }
}