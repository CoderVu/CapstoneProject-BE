package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.model.Chat;
import com.example.CapstoneProject.model.User;
import com.example.CapstoneProject.repository.ChatRepository;
import com.example.CapstoneProject.repository.UserRepository;
import com.example.CapstoneProject.request.ChatRequest;
import com.example.CapstoneProject.response.ChatResponse;
import com.example.CapstoneProject.response.UserResponse;
import com.example.CapstoneProject.service.Interface.IChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService implements IChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Chat saveChat(ChatRequest chatRequest) {
        // Tìm user gửi
        User sender = userRepository.findById(chatRequest.getSender().toString())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // Tìm user nhận
        User receiver = userRepository.findById(chatRequest.getReceiver().toString())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Chat.ChatBuilder chatBuilder = Chat.builder()
                .sender(sender)
                .receiver(receiver)
                .localTime(LocalDateTime.now())
                .message(chatRequest.getMessage())
                .isRead(chatRequest.getIsRead());

        if (chatRequest.getImage() != null) {
            chatBuilder.image(chatRequest.getImage());
        }

        Chat chat = chatBuilder.build();

        // Lưu tin nhắn vào cơ sở dữ liệu
        return chatRepository.save(chat);
    }
    @Override
    public List<ChatResponse> getChatHistory(String senderId, String receiverId) {

        if  (senderId != null && receiverId != null) {
            List<Chat> chats = chatRepository.findBySenderAndReceiver(senderId, receiverId);
            // Chuyển đổi danh sách Chat thành ChatResponse
            return chats.stream().map(chat -> {
                ChatResponse response = new ChatResponse();
                response.setId(chat.getId());
                response.setMessage(chat.getMessage());
                response.setSender(chat.getSender().getId());
                response.setReceiver(chat.getReceiver().getId());
                response.setTimestamp(chat.getLocalTime());
                response.setImageUrl(chat.getImage());
                return response;
            }).collect(Collectors.toList());
        }

        return List.of();
    }
    @Override
    public List<UserResponse> getUsersChattedWith(String senderId) {
        List<User> users = chatRepository.findDistinctUsersBySenderId(senderId);
        return users.stream().map(user -> UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .build()
        ).collect(Collectors.toList());
    }
}