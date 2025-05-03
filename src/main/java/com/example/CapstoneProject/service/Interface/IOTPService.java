package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.model.OTP;
import com.example.CapstoneProject.response.APIResponse;

public interface IOTPService {
    OTP createOTP(String email, String userId);

    APIResponse verifyOTP(String email, String inputOtp);
}
