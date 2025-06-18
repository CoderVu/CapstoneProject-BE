package com.example.CapstoneProject.controller.Public;

import com.example.CapstoneProject.response.JwtResponse;
import com.example.CapstoneProject.service.Interface.IAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class OAuth2CallbackController {
    
    @Autowired
    private IAuthService authService;

    @GetMapping("/login/oauth2/code/google")
    public void oauth2Callback(@AuthenticationPrincipal OAuth2User principal, HttpServletResponse response) throws IOException {
        JwtResponse jwtResponse = authService.oauth2Callback(
                principal.getAttribute("email"),
                principal.getAttribute("name"),
                principal.getAttribute("picture")
        );

        String token = jwtResponse.getToken();
        String html = "<script>" +
                "window.opener.postMessage({ token: '" + token + "' }, '*');" +
                "window.close();" +
                "</script>";
        response.setContentType("text/html");
        response.getWriter().write(html);
    }
} 