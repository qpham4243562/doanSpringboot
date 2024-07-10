package com.bookstore.entity;

import com.bookstore.repository.IUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;
import java.util.stream.Collectors;

public class CustomUserDetail implements UserDetails, OAuth2User {
    private final User user;
    private final IUserRepository userRepository;
    private final DefaultOAuth2User oAuth2User;

    public CustomUserDetail(User user, IUserRepository userRepository, DefaultOAuth2User oAuth2User) {
        this.user = user;
        this.userRepository = userRepository;
        this.oAuth2User = oAuth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        if (oAuth2User != null) {
            return oAuth2User.getAttributes();
        } else {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("id", user.getId().toString());
            attributes.put("name", user.getName());
            attributes.put("username", user.getUsername());
            attributes.put("email", user.getEmail());
            return attributes;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (oAuth2User != null) { // Đăng nhập bằng Google
            authorities.addAll(oAuth2User.getAuthorities());
        } else {
            authorities.addAll(Arrays.stream(userRepository.getRoleOfUser(user.getId()))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
        }
        return authorities;
    }
    @Override
    public String getPassword() {
        return user != null ? user.getPassword() : null; // Kiểm tra null
    }

    @Override
    public String getUsername() {
        return user != null ? user.getUsername() : oAuth2User.getName(); // Kiểm tra null
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Hoặc lấy từ thông tin user nếu có
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Hoặc lấy từ thông tin user nếu có
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Hoặc lấy từ thông tin user nếu có
    }

    @Override
    public boolean isEnabled() {
        return true; // Hoặc lấy từ thông tin user nếu có
    }

    public Long getId() {
        return user != null ? user.getId() : null; // Kiểm tra null
    }
    @Override
    public String getName() {
        if (oAuth2User != null) {
            return oAuth2User.getName(); // Sử dụng tên từ oAuth2User nếu có
        } else {
            return user.getUsername(); // Nếu không, sử dụng username từ User
        }
    }
    // ... Các phương thức khác của OAuth2User (getAttributes, getName, etc.) được ủy quyền cho oAuth2User
}
