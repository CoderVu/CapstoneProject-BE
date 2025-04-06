package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.RegisterRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.JwtResponse;

public interface IAuthService {
    JwtResponse authenticateUser(String username, String password);

    APIResponse registerUser(RegisterRequest registerRequest);

    APIResponse updatePassword(String phoneNumber, String newPassword);

    APIResponse addPhoneNumber(String email, String phoneNumber);

    APIResponse logout(String token);

    boolean isTokenInvalid(String token);

    JwtResponse oauth2Callback(String email, String fullName, String avatar);

}
