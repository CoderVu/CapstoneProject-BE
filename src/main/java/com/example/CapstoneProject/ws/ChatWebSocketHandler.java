package com.example.CapstoneProject.ws;

import com.example.CapstoneProject.model.User;
import com.example.CapstoneProject.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, List<WebSocketSession>> userSessions = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        try {
            Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) payload.get("type");

        if ("identify".equals(type)) {
            String userId = payload.get("userId").toString();
             session.getAttributes().put("userId", userId);
            userSessions.computeIfAbsent(userId, k -> new ArrayList<>()).add(session);
            sendOnlineUsersUpdate(session);
             sendStatusAllUsers(userId, true);
            return;
        }


            String receiverId = payload.get("receiver").toString();
        if (receiverId == null) {
            System.out.println("Receiver is empty");
            return;
        }

            String senderId = payload.get("sender").toString();
            String chatMessage = payload.containsKey("message") ? payload.get("message").toString() : null;

            if (chatMessage == null) {
                System.out.println("Message is empty");
                return;
            }

            User sender = userRepository.findById(senderId).orElse(null);
            if (sender == null) {
                System.out.println("Sender not found: " + senderId);
                return;
            }

            List<WebSocketSession> receiverSessions = userSessions.get(receiverId);
            if (receiverSessions != null) {
                for (WebSocketSession receiverSession : receiverSessions) {
                    if (receiverSession.isOpen()) {
                        receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
                    }
                }
            }

            // Check if sender and receiver are not the same person
            if (!receiverId.equals(senderId)) {
                System.out.println("Receiver id mismatch");
                List<WebSocketSession> senderSessions = userSessions.get(senderId);
                System.out.println("Sender id mismatch");
                if (senderSessions != null) {
                    System.out.println("Sender sessions mismatch");
                    for (WebSocketSession senderSession : senderSessions) {
                        System.out.println("Sender session id mismatch");
                        if (senderSession.isOpen()) {

                            System.out.println("Sender: " + senderId + " Receiver: " + receiverId);
                            System.out.println("Message: " + chatMessage);
                            senderSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            List<WebSocketSession> sessions = userSessions.get(userId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    userSessions.remove(userId);
                    sendStatusAllUsers(userId.toString(), false);
                }
            }
        }
    }

    private void sendOnlineUsersUpdate(WebSocketSession session) throws IOException {
        List<String> onlineUsers = new ArrayList<>(userSessions.keySet());

        Map<String, Object> message = new HashMap<>();
        message.put("type", "onlineUsers");
        message.put("onlineUsers", onlineUsers);

        String jsonMessage = objectMapper.writeValueAsString(message);

        session.sendMessage(new TextMessage(jsonMessage));
    }

    private void sendStatusAllUsers(String userId, boolean status) throws IOException {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "online");
        notification.put("userId", userId);
        notification.put("status", status);

        String jsonMessage = objectMapper.writeValueAsString(notification);

        for (List<WebSocketSession> sessions : userSessions.values()) {
            for (WebSocketSession wsSession : sessions) {
                if (wsSession.isOpen()) {
                    wsSession.sendMessage(new TextMessage(jsonMessage));
                }
            }
        }
    }

    private String getUserIdFromSession(WebSocketSession session) {
        return  session.getAttributes().get("userId").toString();
    }
}