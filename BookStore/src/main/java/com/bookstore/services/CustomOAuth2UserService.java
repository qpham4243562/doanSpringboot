package com.bookstore.services;

import com.bookstore.entity.CustomUserDetail;
import com.bookstore.entity.User;
import com.bookstore.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    @Lazy
    private UserServices userServices;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        User user = userRepository.findByEmail(email);

        if (user == null) {
            String username;
            do {
                username = generateRandomUsername();
            } while (userRepository.findByUsername(username) != null);

            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setUsername(username);
            user.setEnabled(true);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

            user = userServices.saveNewGoogleUser(user);
        }

        if (!user.isEnabled()) { // Assuming 'isEnabled()' checks the 'enable' field
            throw new DisabledException("User account is disabled");
        }

        // Ensure all necessary attributes are present
        attributes.put("id", user.getId().toString());
        attributes.put("email", user.getEmail());
        attributes.put("name", user.getName());
        attributes.put("username", user.getUsername());

        // Use email as the name key if no specific user identifier is provided
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        if (userNameAttributeName == null || userNameAttributeName.isEmpty()) {
            userNameAttributeName = "email";
        }

        DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(
                oauth2User.getAuthorities(), attributes, userNameAttributeName
        );

        return new CustomUserDetail(user, userRepository, defaultOAuth2User);
    }

    private String generateRandomUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
