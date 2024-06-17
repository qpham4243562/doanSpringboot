package com.bookstore.controller;

import com.bookstore.entity.*;
import com.bookstore.repository.IUserRepository;
import com.bookstore.repository.UserPostLikeRepository;
import com.bookstore.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user-posts")
public class UserPostController {

    @Autowired
    private UserPostService userPostService;
    @Autowired
    private UserServices userService;

    @Autowired
    private ClassService classService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private UserPostLikeRepository userPostLikeRepository;
    @Autowired
    private CommentService commentService;

    @GetMapping
    public String getAllUserPosts(@RequestParam(value = "selectedClass", required = false) Long selectedClassId,
                                  @RequestParam(value = "selectedSubject", required = false) Long selectedSubjectId,
                                  Model model) {
        List<User_Post> userPosts;
        if (selectedClassId != null && selectedSubjectId != null) {
            userPosts = userPostService.getUserPostsByClassAndSubject(selectedClassId, selectedSubjectId);
        } else if (selectedClassId != null) {
            userPosts = userPostService.getUserPostsByClass(selectedClassId);
        } else if (selectedSubjectId != null) {
            userPosts = userPostService.getUserPostsBySubject(selectedSubjectId);
        } else {
            userPosts = userPostService.getAllUserPostsWithUser();
        }

        for (User_Post userPost : userPosts) {
            userPost.setCommentCount(commentService.countCommentsByUserPostId(userPost.getId()));
        }

        model.addAttribute("userPosts", userPosts);
        model.addAttribute("classEntities", classService.getAllClasses());
        model.addAttribute("subjectEntities", subjectService.getAllSubjects());
        model.addAttribute("selectedClassId", selectedClassId);
        model.addAttribute("selectedSubjectId", selectedSubjectId);

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
                                 @RequestParam("images") List<MultipartFile> images,
                                 Principal principal) {
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
        String username = principal.getName();
        User user = userService.findByUsername(username);
        userPost.setUser(user);
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

    @PostMapping("/{id}/like")
    public String likeUserPost(@PathVariable Long id, Principal principal) {
        User_Post userPost = userPostService.getUserPostById(id);
        User user = userRepository.findByUsername(principal.getName());

        UserPostLike existingLike = userPostLikeRepository.findByUserAndUserPost(user, userPost);

        if (existingLike != null) {
            userPostLikeRepository.delete(existingLike);
            userPost.setLikes(userPost.getLikes() - 1);
        } else {
            UserPostLike newLike = new UserPostLike();
            newLike.setUser(user);
            newLike.setUserPost(userPost);
            userPostLikeRepository.save(newLike);
            userPost.setLikes(userPost.getLikes() + 1);
        }

        userPostService.updateUserPost(userPost);
        return "redirect:/user-posts";
    }


    @GetMapping("/{postId}")
    public String getUserPost(@PathVariable Long postId, Model model, Principal principal) {
        User_Post userPost = userPostService.getUserPostById(postId);
        User currentUser = userRepository.findByUsername(principal.getName());

        model.addAttribute("userPost", userPost);
        model.addAttribute("comments", commentService.getCommentsByUserPostId(postId));
        model.addAttribute("currentUser", currentUser);
        return "user-post/detail";
    }


    @PostMapping("/{postId}/comments")
    public String addComment(@PathVariable Long postId, @RequestParam("content") String content, Principal principal) {
        User_Post userPost = userPostService.getUserPostById(postId);
        User user = userRepository.findByUsername(principal.getName());

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setUserPost(userPost);

        commentService.addComment(comment);
        return "redirect:/user-posts/" + postId;
    }

    @PostMapping("/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId, @RequestParam("postId") Long postId, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        commentService.deleteComment(commentId, user);
        return "redirect:/user-posts/" + postId;
    }
}
