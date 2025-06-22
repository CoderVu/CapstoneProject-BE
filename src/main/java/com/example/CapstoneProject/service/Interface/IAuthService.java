package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.RegisterRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.JwtResponse;

public interface IAuthService {
    APIResponse authenticateUser(String username, String password);

    APIResponse registerUser(RegisterRequest registerRequest);

    APIResponse registerStaff(RegisterRequest request);

    APIResponse forgetPassword(String email);

    APIResponse verifyForgetPassword(String email, String inputOtp);

    APIResponse updatePassword(String phoneNumber, String newPassword);

    APIResponse changePasswordByIdentifier(String token, String oldPassword, String newPassword);

    APIResponse addPhoneNumber(String email, String phoneNumber);

    APIResponse logout(String token);

    boolean isTokenInvalid(String token);

    JwtResponse oauth2Callback(String email, String fullName, String avatar);

}
