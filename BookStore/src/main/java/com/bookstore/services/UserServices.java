package com.bookstore.services;

import com.bookstore.entity.Comment;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.repository.CommentRepository;
import com.bookstore.repository.IRoleRepository;
import com.bookstore.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserServices {
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CommentRepository commentRepository;
    public void save(User user) {
        boolean isNewUser = user.getId() == null;
        if (isNewUser) {
            user.setEnabled(true); // Default to enabled for new users
        }
        userRepository.save(user);

        if (isNewUser) {
            Long userId = userRepository.getUserIdByUsername(user.getUsername());
            Long roleId = roleRepository.findByName("USER").getId();

            if (roleId != null && userId != null) {
                userRepository.addRoleToUser(userId, roleId);
            }
        }
    }


    @Transactional
    public boolean processForgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        userRepository.save(user);

        sendResetPasswordEmail(email, token);

        return true;
    }

    private void sendResetPasswordEmail(String email, String token) {
        String resetUrl = "http://localhost:8080/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Đặt lại mật khẩu");
        message.setText("Nhấn vào liên kết sau để đặt lại mật khẩu của bạn: " + resetUrl);

        mailSender.send(message);
    }

    public boolean isValidToken(String token) {
        User user = userRepository.findByResetPasswordToken(token);
        return user != null;
    }

    public boolean updatePassword(String token, String password) {
        User user = userRepository.findByResetPasswordToken(token);
        if (user == null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(password));
        user.setResetPasswordToken(null);
        userRepository.save(user);
        return true;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean userHasRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        return user.getRoles().stream().anyMatch(role -> role.getId().equals(roleId));
    }

    public String assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));

        if (userHasRole(userId, roleId)) {
            return "User already has this role";
        }

        user.getRoles().add(role);
        userRepository.save(user);
        return "Role assigned successfully";
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public List<User> searchUsers(String query) {
        return userRepository.findByNameContaining(query);
    }
    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User saveNewGoogleUser(User user) {
        user.setRoles(List.of(roleRepository.findByName("USER")));
        return userRepository.save(user);
    }
    @Transactional
    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    public void enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }



    public List<User> getActiveUsers() {
        return userRepository.findByEnabledTrue();
    }

    @Transactional
    public void updateUser(User updatedUser) {
        User existingUser = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setName(updatedUser.getName());

        userRepository.save(existingUser);
    }
    public List<User> searchUsersByEmail(String email) {
        return userRepository.findUsersByEmailContaining(email);
    }
    @Transactional
    public void updateUserEmail(Long userId, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(newEmail);
        userRepository.save(user);
    }
}
