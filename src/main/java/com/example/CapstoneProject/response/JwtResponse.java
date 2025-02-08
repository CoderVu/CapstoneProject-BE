package com.example.CapstoneProject.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JwtResponse {
    private String id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String avatar;
    private String token;
    private List<String> roles;
}