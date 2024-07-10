package com.bookstore.services;

import com.bookstore.entity.Friend;
import com.bookstore.entity.User;

import com.bookstore.repository.FriendRepository;
import com.bookstore.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendRequestService {
    @Autowired
    private FriendRepository friendRepository;

    public Friend sendFriendRequest(User fromUser, User toUser) {
        Friend existingRequest = friendRepository.findByUserAndFriendAndStatus(fromUser, toUser, "PENDING");
        if (existingRequest != null) {
            throw new IllegalStateException("A friend request has already been sent.");
        }

        // Check if they are already friends
        Friend existingFriendship = friendRepository.findByUserAndFriendAndStatus(fromUser, toUser, "ACCEPTED");
        if (existingFriendship != null) {
            throw new IllegalStateException("You are already friends with this user.");
        }

        Friend friendRequest = new Friend();
        friendRequest.setUser(fromUser);
        friendRequest.setFriend(toUser);
        friendRequest.setStatus("PENDING");
        return friendRepository.save(friendRequest);
    }

    public void acceptFriendRequest(Friend friendRequest) {
        friendRequest.setStatus("ACCEPTED");
        friendRepository.save(friendRequest);

        // Create reverse friendship
        Friend reverseFriendship = new Friend();
        reverseFriendship.setUser(friendRequest.getFriend());
        reverseFriendship.setFriend(friendRequest.getUser());
        reverseFriendship.setStatus("ACCEPTED");
        friendRepository.save(reverseFriendship);
    }

    public void unfriend(User user, User friend) {
        Friend friendship1 = friendRepository.findByUserAndFriend(user, friend);
        Friend friendship2 = friendRepository.findByUserAndFriend(friend, user);

        if (friendship1 != null) {
            friendRepository.delete(friendship1);
        }
        if (friendship2 != null) {
            friendRepository.delete(friendship2);
        }
    }


    public List<Friend> getFriends(User user) {
        return friendRepository.findByUserAndStatus(user, "ACCEPTED");
    }

    public void rejectFriendRequest(Friend friendRequest) {
        friendRequest.setStatus("REJECTED");
        friendRepository.save(friendRequest);
    }


    public Friend findById(Long id) {
        return friendRepository.findById(id).orElse(null);
    }
}
