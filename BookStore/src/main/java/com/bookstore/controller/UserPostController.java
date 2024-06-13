package com.bookstore.controller;

import com.bookstore.entity.User_Post;
import com.bookstore.entity.Image;
import com.bookstore.services.ClassService;
import com.bookstore.services.SubjectService;
import com.bookstore.services.UserPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user-posts")
public class UserPostController {

    @Autowired
    private UserPostService userPostService;

    @Autowired
    private ClassService classService;

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public String getAllUserPosts(Model model) {
        List<User_Post> userPosts = userPostService.getAllUserPosts();
        model.addAttribute("userPosts", userPosts);
        return "user-post/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("userPost", new User_Post());
        model.addAttribute("classEntities", classService.getAllClasses());
        model.addAttribute("subjectEntities", subjectService.getAllSubjects());
        return "user-post/add";
    }

    @PostMapping("/new")
    public String createUserPost(@ModelAttribute User_Post userPost,
                                 @RequestParam("images") List<MultipartFile> images) {
        List<Image> imageList = new ArrayList<>();
        for (MultipartFile file : images) {
            Image image = new Image();
            image.setType(file.getContentType());
            try {
                image.setData(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageList.add(image);
        }
        userPost.setImages(imageList);
        userPostService.createUserPost(userPost);
        return "redirect:/user-posts";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User_Post userPost = userPostService.getUserPostById(id);
        model.addAttribute("userPost", userPost);
        model.addAttribute("classEntities", classService.getAllClasses());
        model.addAttribute("subjectEntities", subjectService.getAllSubjects());
        return "user-post-form";
    }

    @PostMapping("/{id}/edit")
    public String updateUserPost(@PathVariable Long id, @ModelAttribute User_Post userPost,
                                 @RequestParam("images") List<MultipartFile> images) {
        User_Post existingUserPost = userPostService.getUserPostById(id);
        existingUserPost.setContent(userPost.getContent());
        existingUserPost.setClassEntity(userPost.getClassEntity());
        existingUserPost.setSubjectEntity(userPost.getSubjectEntity());

        List<Image> imageList = new ArrayList<>();
        for (MultipartFile file : images) {
            Image image = new Image();
            image.setType(file.getContentType());
            try {
                image.setData(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageList.add(image);
        }
        existingUserPost.setImages(imageList);

        userPostService.updateUserPost(existingUserPost);
        return "redirect:/user-posts";
    }

    @GetMapping("/{id}/delete")
    public String deleteUserPost(@PathVariable Long id) {
        userPostService.deleteUserPost(id);
        return "redirect:/user-posts";
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Image image = userPostService.getImageById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.valueOf(image.getType()));
        return new ResponseEntity<>(image.getData(), headers, HttpStatus.OK);
    }
}