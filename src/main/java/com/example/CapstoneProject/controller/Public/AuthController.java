
    package com.example.CapstoneProject.controller.Public;

    import com.example.CapstoneProject.request.LoginRequest;
    import com.example.CapstoneProject.request.RegisterRequest;
    import com.example.CapstoneProject.response.APIResponse;
    import com.example.CapstoneProject.response.JwtResponse;
    import com.example.CapstoneProject.service.Interface.IAuthService;
    import jakarta.servlet.http.HttpServletResponse;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
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
        private final IAuthService authService;

        @PostMapping("/register")
        public ResponseEntity<APIResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
            APIResponse response = authService.registerUser(request);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }

        @PostMapping("/login")
        public ResponseEntity<APIResponse> authenticateUser(@Valid @RequestBody LoginRequest request) {
            JwtResponse jwtResponse = authService.authenticateUser(request.getPhoneNumber(), request.getPassword());
            if (jwtResponse == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(APIResponse.error(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials"));
            }
            APIResponse apiResponse = new APIResponse(HttpStatus.OK.value(), "Login successful", jwtResponse);
            return ResponseEntity.ok(apiResponse);
        }

        @GetMapping("/oauth2/callback")
        public void oauth2Callback(@AuthenticationPrincipal OAuth2User principal, HttpServletResponse response) throws IOException {
            JwtResponse jwtResponse = authService.oauth2Callback(
                    principal.getAttribute("email"),
                    principal.getAttribute("name"),
                    principal.getAttribute("picture")
            );

            if (jwtResponse == null) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials");
                return;
            }
            // Create redirect URL with token and picture
            String redirectUrl = String.format("https://polite-plant-004c99b1e.6.azurestaticapps.net/oauth2/callback?token=%s",
                    jwtResponse.getToken());
            System.out.println("token: " + jwtResponse.getToken());

            // Redirect to the created URL
            response.sendRedirect(redirectUrl);
        }

//        @GetMapping("/oauth2/callback")
//        public ResponseEntity<APIResponse> oauth2Callback(@AuthenticationPrincipal OAuth2User principal) {
//            JwtResponse jwtResponse = authService.oauth2Callback(
//                    principal.getAttribute("email"),
//                    principal.getAttribute("name"),
//                    principal.getAttribute("picture")
//            );
//            if (jwtResponse == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(APIResponse.error(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials"));
//            }
//            APIResponse apiResponse = new APIResponse(HttpStatus.OK.value(), "Login successful", jwtResponse);
//            return ResponseEntity.ok(apiResponse);
//        }
    }