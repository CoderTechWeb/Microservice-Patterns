package com.techweb.authservice.config;

import com.techweb.authservice.model.User;
import com.techweb.authservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("testuser").isEmpty()) {
                User user = new User();
                user.setUsername("testuser");
                user.setPassword(passwordEncoder.encode("password123")); // BCrypt encoded password
                user.setRole("ROLE_USER");
                userRepository.save(user);
                System.out.println("Test User Created: Username - testuser, Password - password123");
            }
        };
    }
}
