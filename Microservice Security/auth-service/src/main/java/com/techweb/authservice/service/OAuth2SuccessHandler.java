package com.techweb.authservice.service;

import com.techweb.authservice.model.User;
import com.techweb.authservice.repository.UserRepository;
import com.techweb.authservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    public OAuth2SuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        User user = userRepository.findByUsername(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(email);
            newUser.setPassword(""); // OAuth2 users don’t need a password
            newUser.setRole("ROLE_USER"); // Default role
            return userRepository.save(newUser);
        });

        String jwtToken = jwtUtil.generateToken(user.getUsername(), user.getRole());

        response.setContentType("application/json");
        response.getWriter().write("{ \"token\": \"" + jwtToken + "\" }");
        response.getWriter().flush();
    }
}