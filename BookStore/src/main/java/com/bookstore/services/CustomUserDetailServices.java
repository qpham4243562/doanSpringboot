package com.bookstore.services;

import com.bookstore.entity.CustomUserDetail;
import com.bookstore.entity.User;
import com.bookstore.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailServices implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        if (!user.isEnabled()) { // Assuming 'isEnabled()' checks the 'enable' field
            throw new DisabledException("User account is disabled: " + username);
        }

        return new CustomUserDetail(user, userRepository, null);
    }
}