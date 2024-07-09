package com.bookstore.controller;

import com.bookstore.entity.PostReport;
import com.bookstore.entity.User;
import com.bookstore.entity.User_Post;
import com.bookstore.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    private UserServices userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserPostService userPostService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PostReportService postReportService;

    @GetMapping("/roles")
    public String manageRoles(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/manage-roles";
    }

    @PostMapping("/roles/assign")
    public String assignRole(@RequestParam Long userId, @RequestParam Long roleId, Model model) {
        String message = userService.assignRoleToUser(userId, roleId);
        model.addAttribute("message", message);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/manage-roles";
    }

    @GetMapping("/unapproved")
    public String showUnapprovedPosts(Model model) {
        List<User_Post> unapprovedPosts = userPostService.getAllUnapprovedPosts();
        model.addAttribute("unapprovedPosts", unapprovedPosts);
        return "admin/unapproved-posts";
    }

    @PostMapping("/{id}/approve")
    public String approvePost(@PathVariable Long id) {
        User_Post post = userPostService.approvePost(id);
        notificationService.createApprovedNotification(post);
        return "redirect:/admin/unapproved";
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        User_Post post = userPostService.getUserPostById(id);
        userPostService.deleteUserPost(id);
        notificationService.createDeletedNotification(post);
        return "redirect:/admin/unapproved";
    }



    @GetMapping("/reports")
    public String showReports(Model model) {
        List<PostReport> pendingReports = postReportService.getPendingReports();
        model.addAttribute("pendingReports", pendingReports);
        return "admin/reports";
    }

    @PostMapping("/reports/{reportId}/resolve")
    public String resolveReport(@PathVariable Long reportId, @RequestParam String action) {
        PostReport report = postReportService.getReportById(reportId);
        if ("delete".equals(action)) {
            userPostService.deleteUserPost(report.getUserPost().getId());
            notificationService.createDeletedNotification(report.getUserPost());
        }
        postReportService.resolveReport(reportId);
        return "redirect:/admin/reports";
    }

    @PostMapping("/reports/{reportId}/dismiss")
    public String dismissReport(@PathVariable Long reportId) {
        postReportService.dismissReport(reportId);
        return "redirect:/admin/reports";
    }

    @GetMapping("/sbadmin")
    public String trangChuAdmin(Model m){
        return "layoutAdmin";
    }
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/manage-users";
    }

    @PostMapping("/users/{id}/disable")
    public String disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/enable")
    public String enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user) {
        user.setId(id);
        userService.updateUser(user);
        return "redirect:/admin/users";
    }
    @GetMapping("/users/search")
    public String searchUsersByEmail(@RequestParam String email, Model model) {
        List<User> users = userService.searchUsersByEmail(email);
        model.addAttribute("users", users);
        return "admin/manage-users";
    }
    @PostMapping("/users/{id}/edit-email")
    public String editUserEmail(@PathVariable Long id, @RequestParam String newEmail) {
        userService.updateUserEmail(id, newEmail);
        return "redirect:/admin/users";
    }

}
