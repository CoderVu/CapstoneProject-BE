
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

//        @GetMapping("/oauth2/callback")
//        public void oauth2Callback(@AuthenticationPrincipal OAuth2User principal, HttpServletResponse response) throws IOException {
//            JwtResponse jwtResponse = authService.oauth2Callback(
//                    principal.getAttribute("email"),
//                    principal.getAttribute("name"),
//                    principal.getAttribute("picture")
//            );
//
//            if (jwtResponse == null) {
//                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials");
//                return;
//            }
//            // Create redirect URL with token and picture
////            String redirectUrl = String.format("https://polite-plant-004c99b1e.6.azurestaticapps.net/oauth2/callback?token=%s",
////                    jwtResponse.getToken());
//            String redirectUrl = String.format("http://localhost:3000/oauth2/callback?token=%s",
//                    jwtResponse.getToken());
//            // Redirect to the created URL
//            response.sendRedirect(redirectUrl);
//        }

        @GetMapping("/oauth2/callback")
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