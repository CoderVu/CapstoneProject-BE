
    package com.example.CapstoneProject.controller.Public;

    import com.example.CapstoneProject.Request.LoginRequest;
    import com.example.CapstoneProject.Request.RegisterRequest;
    import com.example.CapstoneProject.response.APIResponse;
    import com.example.CapstoneProject.response.JwtResponse;
    import com.example.CapstoneProject.service.Interface.IAuthService;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.security.oauth2.core.user.OAuth2User;
    import org.springframework.web.bind.annotation.*;

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
        public ResponseEntity<APIResponse> oauth2Callback(@AuthenticationPrincipal OAuth2User principal) {
            JwtResponse jwtResponse = authService.oauth2Callback(principal.getAttribute("email"));
            System.out.println(jwtResponse);
            if (jwtResponse == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(APIResponse.error(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials"));
            }
            APIResponse apiResponse = new APIResponse(HttpStatus.OK.value(), "Login successful", jwtResponse);
            return ResponseEntity.ok(apiResponse);
        }
    }