package com.example.CapstoneProject.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderNotificationWebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session);
    }

    public void sendOrderNotification(String orderCode) throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                String notification = objectMapper.writeValueAsString(
                        new OrderNotification("newOrder", orderCode)
                );
                System.out.println("Sending notification: " + notification);
                session.sendMessage(new TextMessage(notification));
            }
        }
    }

    private static class OrderNotification {
        private final String type;
        private final String orderCode;

        public OrderNotification(String type, String orderCode) {
            this.type = type;
            this.orderCode = orderCode;
        }

        public String getType() {
            return type;
        }

        public String getOrderCode() {
            return orderCode;
        }
    }
}