package com.CS3332Group5.MovieTheatreManagementSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // cho phép public các endpoint auth
            .requestMatchers("/api/auth/customer/register", "/api/auth/customer/login").permitAll()
            .requestMatchers("/api/auth/staff/register",    "/api/auth/staff/login").permitAll()
            // cho phép WebSocket endpoints
            .requestMatchers("/ws/**", "/app/**", "/topic/**").permitAll()
            // cho phép static resources nếu có
            .requestMatchers("/customer", "/staff", "/css/**", "/js/**").permitAll()
            // mọi request khác phải authenticated
            .anyRequest().authenticated()
        )
        // **Thêm** dòng này để bật HTTP Basic Auth
        .httpBasic(Customizer.withDefaults())
        // giữ stateless cho REST API
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
