package com.bookstore.services;

import com.bookstore.entity.Friend;
import com.bookstore.entity.Notification;
import com.bookstore.entity.User;
import com.bookstore.entity.User_Post;
import com.bookstore.repository.NotificationRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void createCommentNotification(User_Post userPost, User currentUser) {
        Notification notification = new Notification();
        notification.setUser(userPost.getUser());
        notification.setMessage("Người dùng " + currentUser.getName() + " đã bình luận bài viết của bạn.");
        notification.setRead(false);
        notification.setCreatedAt(new Date());
        notificationRepository.save(notification);
    }

    public void createLikeNotification(User_Post userPost, User currentUser) {
        Notification notification = new Notification();
        notification.setUser(userPost.getUser());
        notification.setMessage("Người dùng " + currentUser.getName() + " đã thích bài viết của bạn.");
        notification.setRead(false);
        notification.setCreatedAt(new Date());
        notificationRepository.save(notification);
    }
    public void createApprovedNotification(User_Post userPost) {
        Notification notification = new Notification();
        notification.setUser(userPost.getUser());
        notification.setMessage("Bài viết của bạn đã được phê duyệt.");
        notification.setRead(false);
        notification.setCreatedAt(new Date());
        notificationRepository.save(notification);
    }

    public void createDeletedNotification(User_Post userPost) {
        Notification notification = new Notification();
        notification.setUser(userPost.getUser());
        notification.setMessage("Bài viết của bạn đã bị xóa do vi phạm quy tắc cộng đồng.");
        notification.setRead(false);
        notification.setCreatedAt(new Date());
        notificationRepository.save(notification);
    }
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalse(user);
    }

    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }
    public void createFriendRequestNotification(Friend friendRequest) {
        Notification notification = new Notification();
        notification.setUser(friendRequest.getFriend());
        notification.setMessage(friendRequest.getUser().getUsername() + " sent you a friend request");
        notification.setType("FRIEND_REQUEST");
        notification.setRelatedUserId(friendRequest.getUser().getId());
        notification.setFriendRequestId(friendRequest.getId());
        notification.setCreatedAt(new Date());
        notification.setRead(false);
        notificationRepository.save(notification);
    }
    public List<Notification> getAllNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public void markAllNotificationsAsRead(User user) {
        List<Notification> unreadNotifications = notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(unreadNotifications);
    }
    @Transactional
    public void deleteNotificationByFriendRequest(Friend friendRequest) {
        notificationRepository.deleteByTypeAndRelatedUserId("FRIEND_REQUEST", friendRequest.getUser().getId());
    }
}