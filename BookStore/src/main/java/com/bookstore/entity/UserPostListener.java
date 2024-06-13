package com.bookstore.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.util.Date;

public class UserPostListener {

    @PrePersist
    public void prePersist(User_Post userPost) {
        if (userPost.getCreatedAt() == null) {
            userPost.setCreatedAt(new Date());
        }
        userPost.setUpdatedAt(new Date());
    }

    @PreUpdate
    public void preUpdate(User_Post userPost) {
        userPost.setUpdatedAt(new Date());
    }
}

