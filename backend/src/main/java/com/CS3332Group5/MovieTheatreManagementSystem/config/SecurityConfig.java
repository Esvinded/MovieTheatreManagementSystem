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
                .requestMatchers(
                    "/api/auth/customer/register",
                    "/api/auth/customer/login",
                    "/api/auth/staff/register",
                    "/api/auth/staff/login",
                    "/api/auth/logout"
                ).permitAll()
                // Allow public access to common pages and static resources
                .requestMatchers("/customer", "/staff", "/css/**", "/js/**").permitAll()
                // Allow authenticated users to change their password
                .requestMatchers("/api/auth/change-password").authenticated()
                // Secure all other endpoints
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)); // Use session-based authentication

        return http.build();
    }
}
