package com.example.CapstoneProject.security;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class SocialConfig {

    private String facebookClientId = System.getenv("FACEBOOK_CLIENT_ID");
    private String facebookClientSecret = System.getenv("FACEBOOK_CLIENT_SECRET");
    private String googleClientId = System.getenv("GOOGLE_CLIENT_ID");
    private String googleClientSecret = System.getenv("GOOGLE_CLIENT_SECRET");
}