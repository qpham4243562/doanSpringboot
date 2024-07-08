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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserServices userService;

    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private UserPostService userPostService;

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
    public String showProfile(@RequestParam(required = false) Long userId, Model model, Authentication authentication) {
        User user;
        boolean isOwnProfile;
        if (userId != null) {
            user = userService.findById(userId);
            if (user == null) {
                return "redirect:/";  // or to an error page
            }
            isOwnProfile = authentication != null && user.getUsername().equals(authentication.getName());
        } else {
            String username = authentication.getName();
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
    public String updateProfile(@ModelAttribute("profileDTO") ProfileDTO profileDTO, Authentication authentication) throws IOException {
        User currentUser = userService.findByUsername(authentication.getName());

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

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if (!userService.isValidToken(token)) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            return "user/reset-password";
        }
        model.addAttribute("token", token);
        return "user/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password, Model model) {
        if (userService.updatePassword(token, password)) {
            model.addAttribute("message", "Mật khẩu đã được đặt lại thành công!");
        } else {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
        }
        return "user/reset-password";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "user/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        boolean result = userService.processForgotPassword(email);
        if (result) {
            model.addAttribute("message", "Đã gửi email đặt lại mật khẩu!");
        } else {
            model.addAttribute("error", "Email không tồn tại trong hệ thống!");
        }
        return "user/forgot-password";
    }

    @GetMapping("/oauth2/success")
    public String handleOAuth2Success(Model model, @AuthenticationPrincipal OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        User user = userService.findByEmail(email);
        if (user != null) {
            // Lưu thông tin người dùng vào Principal
            Authentication authentication = new UsernamePasswordAuthenticationToken(oauth2User, null, oauth2User.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/user-posts";
        } else {
            model.addAttribute("error", "Không thể đăng nhập bằng Google. Vui lòng thử lại.");
            return "user/login";
        }
    }

}
