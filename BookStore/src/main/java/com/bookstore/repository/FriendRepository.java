package com.bookstore.repository;

import com.bookstore.entity.Friend;
import com.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Friend findByUserAndFriend(User user, User friend);
    List<Friend> findByUserAndStatus(User user, String status);
    List<Friend> findByFriendAndStatus(User friend, String status);

}