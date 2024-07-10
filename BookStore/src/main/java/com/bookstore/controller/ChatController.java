package com.bookstore.controller;

import com.bookstore.entity.Friend;
import com.bookstore.entity.Message;
import com.bookstore.entity.User;
import com.bookstore.repository.IUserRepository;
import com.bookstore.services.FriendRequestService;
import com.bookstore.services.MessageService;
import com.bookstore.services.NotificationService;
import com.bookstore.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private UserServices userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private FriendRequestService friendRequestService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private IUserRepository userRepository;
    @ModelAttribute
    public void addAttributes(Model model, Principal principal) {
        if (principal != null) {
            User currentUser = userRepository.findByUsername(principal.getName());
            int unreadNotificationCount = notificationService.getUnreadNotifications(currentUser).size();
            model.addAttribute("unreadNotificationCount", unreadNotificationCount);
        }
    }
    @GetMapping("/chat")
    public String getChatPage(Model model, Principal principal) {
        String username = principal.getName();
        User currentUser = userService.findByUsername(username);
        List<Friend> friends = friendRequestService.getFriends(currentUser);

        model.addAttribute("friends", friends);
        model.addAttribute("currentUser", currentUser);

        return "chat/chat";
    }

    @GetMapping("/chat/{recipientId}")
    public String getChatContent(@PathVariable Long recipientId, Model model, Principal principal) {
        String username = principal.getName();
        User sender = userService.findByUsername(username);
        User recipient = userService.findById(recipientId);

        List<Message> messages = messageService.getConversation(sender.getId(), recipientId);

        model.addAttribute("messages", messages);
        model.addAttribute("sender", sender);
        model.addAttribute("recipient", recipient);

        return "chatContent :: chatFragment";
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message chatMessage) {
        User sender = userService.findById(chatMessage.getSender().getId());
        chatMessage.getSender().setName(sender.getName());  // Ensure the sender's name is set
        return messageService.save(chatMessage);
    }
}
