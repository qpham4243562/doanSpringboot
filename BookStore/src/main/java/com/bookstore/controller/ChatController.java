package com.bookstore.controller;

import com.bookstore.entity.Message;
import com.bookstore.entity.User;
import com.bookstore.services.MessageService;
import com.bookstore.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserServices userService;

    @GetMapping("/chat")
    public String chat(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Message> messages = messageService.getAllMessages();
        model.addAttribute("messages", messages);
        model.addAttribute("user", user);
        return "chat";
    }

    @PostMapping("/send-message")
    public String sendMessage(@RequestParam("text") String text, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        Message message = new Message(user, text);
        messageService.saveMessage(message);
        return "redirect:/chat";
    }
}