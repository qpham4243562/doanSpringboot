package com.bookstore.controller;

import com.bookstore.dto.ProfileDTO;
import com.bookstore.entity.User;
import com.bookstore.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserServices userService;

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                model.addAttribute(error.getField() + "_error", error.getDefaultMessage());
            }
            return "user/register";
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userService.save(user);
        return "redirect:/login";
    }
    @GetMapping("/profile")
    public String showProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);

        // Chuyển đổi dữ liệu ảnh thành chuỗi Base64
        String imageBase64 = null;
        if (user.getImage() != null) {
            imageBase64 = Base64.getEncoder().encodeToString(user.getImage());
        }

        model.addAttribute("user", user);
        model.addAttribute("imageBase64", imageBase64);

        ProfileDTO profileDTO = new ProfileDTO();
        model.addAttribute("profileDTO", profileDTO);

        return "user/profile";
    }
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("profileDTO") ProfileDTO profileDTO, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        User currentUser = userService.findByUsername(userDetails.getUsername());

        if (profileDTO.getName() != null && !profileDTO.getName().isEmpty()) {
            currentUser.setName(profileDTO.getName());
        }

        if (profileDTO.getImage() != null && !profileDTO.getImage().isEmpty()) {
            byte[] imageBytes = profileDTO.getImage().getBytes();
            currentUser.setImage(imageBytes);
        }

        userService.save(currentUser);
        return "redirect:/profile";
    }
}
