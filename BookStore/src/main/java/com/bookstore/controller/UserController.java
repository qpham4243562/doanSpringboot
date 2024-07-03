package com.bookstore.controller;

import com.bookstore.dto.ProfileDTO;
import com.bookstore.entity.Friend;
import com.bookstore.entity.User;
import com.bookstore.entity.User_Post;
import com.bookstore.services.FriendRequestService;
import com.bookstore.services.UserPostService;
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
    @Autowired
    private FriendRequestService friendRequestService;

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @Autowired
    private UserPostService userPostService;
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
    public String showProfile(@RequestParam(required = false) Long userId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user;
        boolean isOwnProfile;
        if (userId != null) {
            user = userService.findById(userId);
            if (user == null) {
                return "redirect:/";  // or to an error page
            }
            isOwnProfile = user.getUsername().equals(userDetails.getUsername());
        } else {
            String username = userDetails.getUsername();
            user = userService.findByUsername(username);
            isOwnProfile = true; // Because it's the logged-in user accessing their own profile
        }

        // Convert image to Base64 for current user's avatar
        String imageBase64 = null;
        if (user.getImage() != null) {
            imageBase64 = Base64.getEncoder().encodeToString(user.getImage());
        }

        // Get user's posts
        List<User_Post> userPosts = userPostService.getUserPostsByUser(user);

        // Get followed posts
        List<User_Post> followedPosts = user.getFollowedPosts();

        // Get friend requests
        List<Friend> friends = friendRequestService.getFriends(user);

        // Add current user's profile information to the model
        model.addAttribute("user", user);
        model.addAttribute("imageBase64", imageBase64);
        model.addAttribute("userPosts", userPosts);
        model.addAttribute("followedPosts", followedPosts);
        model.addAttribute("friends", friends);
        model.addAttribute("isOwnProfile", isOwnProfile);

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
