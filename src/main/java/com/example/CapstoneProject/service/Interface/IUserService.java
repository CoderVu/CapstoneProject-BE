package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.response.JwtResponse;
import com.example.CapstoneProject.response.UserResponse;

public interface IUserService {


    JwtResponse getUserInfo(String token);

    UserResponse getUserInfoById(String id);
}
