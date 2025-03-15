package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.response.JwtResponse;
import com.example.CapstoneProject.response.UserResponse;

import java.util.List;

public interface IUserService {


    JwtResponse getUserInfo(String token);

    UserResponse getUserInfoById(String id);

    List<UserResponse> getAllUser();
}
