package com.CS3332Group5.MovieTheatreManagementSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF protection for APIs
            .authorizeHttpRequests(auth -> auth
                // Allow public access to customer and staff authentication endpoints
                .requestMatchers("/api/auth/customer/register", "/api/auth/customer/login").permitAll()
                .requestMatchers("/api/auth/staff/register", "/api/auth/staff/login").permitAll()
                // Secure all other endpoints
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Stateless sessions for APIs

        return http.build();
    }
}
