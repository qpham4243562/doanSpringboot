package com.bookstore.controller;

import com.bookstore.entity.Friend;
import com.bookstore.entity.User;

import com.bookstore.services.FriendRequestService;
import com.bookstore.services.NotificationService;
import com.bookstore.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/friend")
public class FriendRequestController {

    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private UserServices userService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<?> sendFriendRequest(@RequestParam("friendId") Long friendId, Principal principal) {
        User fromUser = userService.findByUsername(principal.getName());
        User toUser = userService.findById(friendId);

        // Check if the user is trying to send a friend request to themselves
        if (fromUser.getId().equals(toUser.getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "You cannot send a friend request to yourself"));
        }

        Friend friendRequest = friendRequestService.sendFriendRequest(fromUser, toUser);
        notificationService.createFriendRequestNotification(friendRequest);
        return ResponseEntity.ok(Map.of("message", "Friend request sent successfully"));
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestParam("friendRequestId") Long friendRequestId, Principal principal) {
        Friend friendRequest = friendRequestService.findById(friendRequestId);
        if (friendRequest != null) {
            friendRequestService.acceptFriendRequest(friendRequest);
            return ResponseEntity.ok(Map.of("message", "Friend request accepted"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Friend request not found"));
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<?> rejectFriendRequest(@RequestParam("friendRequestId") Long friendRequestId, Principal principal) {
        Friend friendRequest = friendRequestService.findById(friendRequestId);
        if (friendRequest != null) {
            friendRequestService.rejectFriendRequest(friendRequest);
            return ResponseEntity.ok(Map.of("message", "Friend request rejected"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Friend request not found"));
        }
    }

    @PostMapping("/unfriend")
    public ResponseEntity<?> unfriend(@RequestParam("friendId") Long friendId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        User friend = userService.findById(friendId);
        friendRequestService.unfriend(user, friend);
        return ResponseEntity.ok(Map.of("message", "Unfriended successfully"));
    }
}