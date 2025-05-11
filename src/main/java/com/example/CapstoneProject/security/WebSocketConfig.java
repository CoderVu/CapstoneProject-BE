package com.example.CapstoneProject.security;
import com.example.CapstoneProject.ws.OrderNotificationWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.CapstoneProject.ws.ChatWebSocketHandler;

@Configuration
@EnableWebSocket

public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final OrderNotificationWebSocketHandler orderNotificationWebSocketHandler;

    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler,
                           OrderNotificationWebSocketHandler orderNotificationWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.orderNotificationWebSocketHandler = orderNotificationWebSocketHandler;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws").setAllowedOrigins("*");
        registry.addHandler(orderNotificationWebSocketHandler, "/ws/orders").setAllowedOrigins("*");
    }
}