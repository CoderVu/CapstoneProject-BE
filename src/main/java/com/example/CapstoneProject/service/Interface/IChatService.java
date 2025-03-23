package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.model.Chat;
import com.example.CapstoneProject.request.ChatRequest;
import com.example.CapstoneProject.response.ChatResponse;
import com.example.CapstoneProject.response.UserResponse;

import java.util.List;

public interface IChatService {
    Chat saveChat(ChatRequest chatRequest);

    List<ChatResponse> getChatHistory(String senderId, String receiverId);

    List<UserResponse> getUsersChattedWith(String senderId);
}
