package com.CS3332Group5.MovieTheatreManagementSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


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
                // Allow authenticated users to change their password and logout
                .requestMatchers("/api/auth/change-password", "/api/auth/logout").authenticated()
                // Allow public access to customer and staff authentication endpoints
                .requestMatchers(
                    "/api/auth/**"
                ).permitAll()
                // Profile endpoints: require login
                .requestMatchers(
                    "/api/customer/**"
                ).authenticated()
                // VNPay return endpoint must be public for payment result
                .requestMatchers("/api/bookings/vnpay-return").permitAll()
                // Static resources
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                // Secure all other endpoints
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)); // Use session-based authentication

        return http.build();
    }
}