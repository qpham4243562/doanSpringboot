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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private PostReportService postReportService;


    @ModelAttribute
    public void addAttributes(Model model, Principal principal) {
        if (principal != null) {
            User currentUser = userRepository.findByUsername(principal.getName());
            int unreadNotificationCount = notificationService.getUnreadNotifications(currentUser).size();
            model.addAttribute("unreadNotificationCount", unreadNotificationCount);
        }
    }
    @GetMapping
    public String getAllUserPosts(@RequestParam(value = "selectedClass", required = false) Long selectedClassId,
                                  @RequestParam(value = "selectedSubject", required = false) Long selectedSubjectId,
                                  Model model,
                                  Principal principal) {
        List<User_Post> userPosts;
        if (selectedClassId != null && selectedSubjectId != null) {
            userPosts = userPostService.getUserPostsByClassAndSubject(selectedClassId, selectedSubjectId);
        } else if (selectedClassId != null) {
            userPosts = userPostService.getUserPostsByClass(selectedClassId);
        } else if (selectedSubjectId != null) {
            userPosts = userPostService.getUserPostsBySubject(selectedSubjectId);
        } else {
            userPosts = userPostService.getAllApprovedUserPosts();
        }

        userPosts = userPosts.stream().filter(User_Post::isApproved).collect(Collectors.toList());

        for (User_Post userPost : userPosts) {
            userPost.setCommentCount(commentService.countCommentsByUserPostId(userPost.getId()));
        }

        userPosts.sort((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()));

        // Lấy thông tin người dùng hiện tại
        User currentUser = userService.findByUsername(principal.getName());

        // Lấy danh sách các bài viết đã follow
        List<User_Post> followedPosts = currentUser.getFollowedPosts();

        model.addAttribute("userPosts", userPosts);
        model.addAttribute("classEntities", classService.getAllClasses());
        model.addAttribute("subjectEntities", subjectService.getAllSubjects());
        model.addAttribute("selectedClassId", selectedClassId);
        model.addAttribute("selectedSubjectId", selectedSubjectId);
        model.addAttribute("followedPosts", followedPosts);
        model.addAttribute("currentUser", currentUser);

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
        return "user-post/edit";
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
    public ResponseEntity<?> likeUserPost(@PathVariable Long id, Principal principal) {
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
            notificationService.createLikeNotification(userPost, user);
        }

        userPostService.updateUserPost(userPost);

        // Return JSON response with updated like count
        return ResponseEntity.ok(userPost.getLikes());
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
        notificationService.createCommentNotification(userPost, user);
        return "redirect:/user-posts/" + postId;
    }

    @PostMapping("/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId, @RequestParam("postId") Long postId, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        commentService.deleteComment(commentId, user);
        return "redirect:/user-posts/" + postId;
    }
    @GetMapping("/notifications")
    public String getNotifications(Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName());
        List<Notification> notifications = notificationService.getUnreadNotifications(currentUser);
        model.addAttribute("notifications", notifications);
        return "user-post/notifications";
    }
    @PostMapping("/notifications/{id}/mark-as-read")
    public String markNotificationAsRead(@PathVariable Long id) {
        notificationService.markNotificationAsRead(id);
        return "redirect:/user-posts/notifications";
    }
    @PostMapping("/{id}/report")
    public String reportUserPost(@PathVariable Long id, @RequestParam String reason, Principal principal) {
        User_Post userPost = userPostService.getUserPostById(id);
        User reporter = userRepository.findByUsername(principal.getName());
        postReportService.createReport(userPost, reporter, reason);
        return "redirect:/user-posts/" + id;
    }
    @PostMapping("/{id}/follow")
    public String followUserPost(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User_Post userPost = userPostService.getUserPostById(id);
        User currentUser = userRepository.findByUsername(principal.getName());

        if (userPost.getUser().equals(currentUser)) {
            redirectAttributes.addFlashAttribute("message", "Bạn không thể theo dõi bài viết của chính mình.");
        } else if (!currentUser.getFollowedPosts().contains(userPost)) {
            currentUser.followPost(userPost);
            userService.save(currentUser);
            redirectAttributes.addFlashAttribute("message", "Đã theo dõi bài viết thành công.");
        } else {
            redirectAttributes.addFlashAttribute("message", "Bạn đã theo dõi bài viết này rồi.");
        }

        return "redirect:/user-posts/" + id;
    }

    @PostMapping("/{id}/unfollow")
    public String unfollowUserPost(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User_Post userPost = userPostService.getUserPostById(id);
        User currentUser = userRepository.findByUsername(principal.getName());

        if (userPost.getUser().equals(currentUser)) {
            redirectAttributes.addFlashAttribute("message", "Bạn không thể hủy theo dõi bài viết của chính mình.");
        } else if (currentUser.getFollowedPosts().contains(userPost)) {
            currentUser.unfollowPost(userPost);
            userService.save(currentUser);
            redirectAttributes.addFlashAttribute("message", "Đã hủy theo dõi bài viết thành công.");
        } else {
            redirectAttributes.addFlashAttribute("message", "Bạn chưa theo dõi bài viết này.");
        }

        return "redirect:/user-posts/" + id;
    }
    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model, Principal principal) {
        List<User> users = userService.searchUsers(query);
        List<User_Post> userPosts = userPostService.searchPosts(query);

        // Get current user and their followed posts
        User currentUser = userService.findByUsername(principal.getName());
        List<User_Post> followedPosts = currentUser.getFollowedPosts();

        // Adding attributes to the model
        model.addAttribute("users", users);
        model.addAttribute("userPosts", userPosts);
        model.addAttribute("followedPosts", followedPosts);
        model.addAttribute("currentUser", currentUser);

        return "user-post/search";
    }

}
