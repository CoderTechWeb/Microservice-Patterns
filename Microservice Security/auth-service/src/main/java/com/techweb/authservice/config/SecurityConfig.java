package com.techweb.authservice.config;

import com.techweb.authservice.filter.JwtAuthenticationFilter;
import com.techweb.authservice.repository.UserRepository;
import com.techweb.authservice.service.OAuth2SuccessHandler;
import com.techweb.authservice.service.UserService;
import com.techweb.authservice.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final UserService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public SecurityConfig(UserService userService, JwtAuthenticationFilter jwtAuthenticationFilter, JwtUtil jwtUtil, UserRepository userRepository) {
        this.userService = userService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/refresh", "/oauth2/**").permitAll() // Allow OAuth2 endpoints
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler()) // Handle OAuth2 login success
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(jwtUtil, userRepository); // Pass JwtUtil to handler
    }
}
