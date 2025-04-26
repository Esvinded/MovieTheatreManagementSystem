package com.CS3332Group5.MovieTheatreManagementSystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");  // prefix cho messages từ server -> client
        config.setApplicationDestinationPrefixes("/app");  // prefix cho messages từ client -> server
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")  // WebSocket endpoint
                .setAllowedOrigins("*")  // cho phép cross-origin
                .withSockJS();  // fallback nếu WebSocket không được hỗ trợ
    }
} 