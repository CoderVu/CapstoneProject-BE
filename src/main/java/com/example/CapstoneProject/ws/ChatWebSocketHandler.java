package com.example.CapstoneProject.ws;

import com.example.CapstoneProject.model.User;
import com.example.CapstoneProject.repository.UserRepository;
import com.example.CapstoneProject.request.ChatRequest;
import com.example.CapstoneProject.service.Interface.IChatService;
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
    @Autowired
    private IChatService chatService;
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        try {
            Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) payload.get("type");

            if ("identify".equals(type)) {
                String userId = payload.getOrDefault("userId", "").toString();
                if (!userId.isEmpty()) {
                    session.getAttributes().put("userId", userId);
                    userSessions.computeIfAbsent(userId, k -> new ArrayList<>()).add(session);
                    sendOnlineUsersUpdate(session);
                    sendStatusAllUsers(userId, true);
                } else {
                    System.out.println("User ID is null in identify message");
                }
                return;
            }

            // Get sender and receiver safely
            String senderId = payload.getOrDefault("sender", "").toString();
            String receiverId = payload.getOrDefault("receiver", "").toString();

            if (senderId.isEmpty() || receiverId.isEmpty()) {
                System.out.println("Sender or receiver is empty");
                return;
            }

            // Get message and imageUrl safely
            String chatMessage = payload.containsKey("message") && payload.get("message") != null
                    ? payload.get("message").toString()
                    : null;
            String imageUrl = payload.containsKey("imageUrl") && payload.get("imageUrl") != null
                    ? payload.get("imageUrl").toString()
                    : null;

            // Ensure at least one of them exists
            if (chatMessage == null && imageUrl == null) {
                System.out.println("Both message and image are empty");
                return;
            }

            // Validate sender
            User sender = userRepository.findById(senderId).orElse(null);
            if (sender == null) {
                System.out.println("Sender not found: " + senderId);
                return;
            }

            // Save chat
            ChatRequest chatRequest = new ChatRequest();
            chatRequest.setSender(senderId);
            chatRequest.setReceiver(receiverId);
            chatRequest.setMessage(chatMessage);
            chatRequest.setImage(imageUrl);
            chatRequest.setIsRead(false);
            chatService.saveChat(chatRequest);

            // Send to receiver
            List<WebSocketSession> receiverSessions = userSessions.get(receiverId);
            if (receiverSessions != null) {
                for (WebSocketSession receiverSession : receiverSessions) {
                    if (receiverSession.isOpen()) {
                        receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
                    }
                }
            }

            // Send to sender (if different)
            if (!receiverId.equals(senderId)) {
                List<WebSocketSession> senderSessions = userSessions.get(senderId);
                if (senderSessions != null) {
                    for (WebSocketSession senderSession : senderSessions) {
                        if (senderSession != null && senderSession.isOpen()) {
                            senderSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
                        }
                    }
                }
            }

            System.out.println("Message sent: " + payload);
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
        Object userIdObj = session.getAttributes().get("userId");
        return userIdObj != null ? userIdObj.toString() : null;
    }
}