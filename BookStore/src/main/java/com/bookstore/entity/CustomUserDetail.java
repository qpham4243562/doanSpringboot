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
        if (oAuth2User != null) {
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
        return user != null ? user.getPassword() : null;
    }

    @Override
    public String getUsername() {
        return user != null ? user.getUsername() : oAuth2User.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return user != null ? user.getId() : null;
    }
    @Override
    public String getName() {
        if (oAuth2User != null) {
            return oAuth2User.getName();
        } else {
            return user.getUsername();
        }
    }

}
