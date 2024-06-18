package com.bookstore.controller;

import com.bookstore.entity.User_Post;
import com.bookstore.services.RoleService;
import com.bookstore.services.UserPostService;
import com.bookstore.services.UserServices;
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
        userPostService.approvePost(id);
        return "redirect:/admin/unapproved";
    }
}
