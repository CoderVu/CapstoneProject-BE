package com.example.CapstoneProject.response;

import com.example.CapstoneProject.model.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class UserResponse {
    private String id;
    private String phoneNumber;
    private String fullName;
    private String avatar;
    private String email;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private RoleResponse role;

}
