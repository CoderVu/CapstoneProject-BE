package com.example.CapstoneProject.security;

import com.example.CapstoneProject.response.JwtResponse;
import com.example.CapstoneProject.service.Interface.IAuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final IAuthService authService;

    public OAuth2LoginSuccessHandler(@Lazy IAuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        JwtResponse jwtResponse = authService.oauth2Callback(
                oAuth2User.getAttribute("email"),
                oAuth2User.getAttribute("name"),
                oAuth2User.getAttribute("picture")
        );

        String token = jwtResponse.getToken();

        // Gửi token về popup
        String html = "<script>" +
                "window.opener.postMessage({ token: '" + token + "' }, '*');" +
                "window.close();" +
                "</script>";

        response.setContentType("text/html");
        response.getWriter().write(html);
    }
}
