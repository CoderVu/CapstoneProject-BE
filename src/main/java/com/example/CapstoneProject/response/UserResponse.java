package com.example.CapstoneProject.response;

import com.example.CapstoneProject.model.User;
import lombok.Data;

import java.time.LocalDateTime;
@Data
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
    public UserResponse(User user) {
        this.id = user.getId();
        this.phoneNumber = user.getPhoneNumber();
        this.fullName = user.getFullName();
        this.avatar = user.getAvatar();
        this.email = user.getEmail();
        this.address = user.getAddress();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.role = new RoleResponse(user.getRole().getId(), user.getRole().getName());
    }
}
