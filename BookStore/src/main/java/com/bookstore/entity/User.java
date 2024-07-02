    package com.bookstore.entity;

    import com.bookstore.validator.ValidUsername;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.Size;
    import lombok.Data;
    import org.apache.commons.codec.binary.Base64;

    import java.util.ArrayList;
    import java.util.List;

    @Data
    @Entity
    @Table(name = "user")
    public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "username", length = 50, nullable = false, unique = true)
        @NotBlank(message = "Tên đăng nhập không được để trống")
        @Size(max = 50, message = "Tên đăng nhập phải ít hơn 50 ký tự")
        private String username;

        @Column(name = "password", length = 250, nullable = false)
        @NotBlank(message = "Mật khẩu không được để trống")
        private String password;

        @Column(name = "email", length = 50, nullable = false, unique = true)
        @NotBlank(message = "Email không được để trống")
        @Size(max = 50, message = "Email phải ít hơn 50 ký tự")
        private String email;

        @Column(name = "name", length = 50, nullable = false)
        @Size(max = 50, message = "Tên của bạn phải ít hơn 50 ký tự")
        @NotBlank(message = "Tên của bạn không được để trống")
        private String name;

        @Column(name = "reset_password_token")
        private String resetPasswordToken;

        @Column(name = "image")
        @Lob
        private byte[] image;

        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(
                name = "user_role",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id")
        )
        private List<Role> roles;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<UserPostLike> userPostLikes;

        public List<UserPostLike> getUserPostLikes() {
            return userPostLikes;
        }
        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<User_Post> userPosts;
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(
                name = "user_followed_posts",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "post_id")
        )
        private List<User_Post> followedPosts = new ArrayList<>();

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Friend> friends;

        @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Friend> friendRequests;

        // Methods to handle friend requests
        public void addFriendRequest(Friend friendRequest) {
            this.friendRequests.add(friendRequest);
        }

        public void removeFriendRequest(Friend friendRequest) {
            this.friendRequests.remove(friendRequest);
        }

        public void acceptFriendRequest(Friend friend) {
            this.friends.add(friend);
        }

        public void rejectFriendRequest(Friend friend) {
            this.removeFriendRequest(friend);
        }

        public void unfriend(Friend friend) {
            this.friends.remove(friend);
        }

        public void setUserPostLikes(List<UserPostLike> userPostLikes) {
            this.userPostLikes = userPostLikes;
        }
        public String getBase64Image() {
            return Base64.encodeBase64String(image);
        }
        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Notification> notifications;

        public List<User_Post> getFollowedPosts() {
            return followedPosts;
        }

        public void setFollowedPosts(List<User_Post> followedPosts) {
            this.followedPosts = followedPosts;
        }

        public void followPost(User_Post post) {
            this.followedPosts.add(post);
        }

        public void unfollowPost(User_Post post) {
            this.followedPosts.remove(post);
        }

    }
