package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.response.UserResponse;

public interface IUserService {
    UserResponse getUserInfo(String token);
}
