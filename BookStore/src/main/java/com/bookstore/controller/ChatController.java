package com.bookstore.controller;

import com.bookstore.entity.Friend;
import com.bookstore.entity.Message;
import com.bookstore.entity.User;
import com.bookstore.services.FriendRequestService;
import com.bookstore.services.MessageService;
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
        return messageService.save(chatMessage);
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Message addUser(@Payload Message chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        if (chatMessage == null || chatMessage.getSender() == null) {
            System.err.println("Unable to add user: chatMessage or chatMessage.getSender() is null");
            return null;
        }
        if (headerAccessor != null && headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender().getUsername());
        } else {
            // Log the error or handle it appropriately
            System.err.println("Unable to add user: headerAccessor or its session attributes are null");
        }
        return chatMessage;
    }
}
