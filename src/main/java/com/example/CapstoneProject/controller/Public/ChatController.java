package com.example.CapstoneProject.controller.Public;

import com.example.CapstoneProject.response.ChatResponse;
import com.example.CapstoneProject.response.UserResponse;
import com.example.CapstoneProject.service.Interface.IChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/chat")
public class ChatController {

    @Autowired
    private IChatService chatService;

    @GetMapping("/history")
    public ResponseEntity<List<ChatResponse>> getChatHistory(@RequestParam String senderId, @RequestParam String receiverId) {
        return ResponseEntity.ok(chatService.getChatHistory(senderId, receiverId));
    }
    @GetMapping("/users/chatted-with")
    public ResponseEntity<List<UserResponse>> getUsersChattedWith(@RequestParam String senderId) {
        return ResponseEntity.ok(chatService.getUsersChattedWith(senderId));
    }
}