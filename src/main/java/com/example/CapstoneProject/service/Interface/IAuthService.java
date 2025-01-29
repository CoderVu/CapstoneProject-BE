package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.Request.RegisterRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.JwtResponse;
import com.example.CapstoneProject.response.UserResponse;

public interface IAuthService {
    JwtResponse authenticateUser(String username, String password);

    APIResponse registerUser(RegisterRequest registerRequest);

    void logout(String token);

    boolean isTokenInvalid(String token);

    JwtResponse oauth2Callback(String email);

}
