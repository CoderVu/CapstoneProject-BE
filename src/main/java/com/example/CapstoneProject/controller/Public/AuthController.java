package com.example.CapstoneProject.controller.Public;

import com.example.CapstoneProject.request.LoginRequest;
import com.example.CapstoneProject.request.RegisterRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.JwtResponse;
import com.example.CapstoneProject.service.Interface.IAuthService;
import com.example.CapstoneProject.service.Interface.IOTPService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private IAuthService authService;
    @Autowired
    private IOTPService otpService;


    @PostMapping("/register")
    public ResponseEntity<APIResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        APIResponse response = authService.registerUser(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @PostMapping("/register/verify")
    public ResponseEntity<APIResponse> verifyUser(@RequestParam String email, @RequestParam String code) {
        APIResponse response = otpService.verifyOTP(email, code);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<APIResponse> authenticateUser(@Valid @RequestBody LoginRequest request) {
        APIResponse apiResponse = authService.authenticateUser(request.getPhoneNumber(), request.getPassword());
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<APIResponse> forgetPassword(@RequestParam String email) {
        APIResponse response = authService.forgetPassword(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @PostMapping("/forget-password/verify")
    public ResponseEntity<APIResponse> verifyForgetPassword(@RequestParam String email, @RequestParam String code) {
        APIResponse response = authService.verifyForgetPassword(email, code);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


}