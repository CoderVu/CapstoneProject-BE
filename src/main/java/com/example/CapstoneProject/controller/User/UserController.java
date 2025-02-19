package com.example.CapstoneProject.controller.User;

import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.JwtResponse;
import com.example.CapstoneProject.response.UserResponse;
import com.example.CapstoneProject.service.Interface.IAuthService;
import com.example.CapstoneProject.service.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IAuthService authService;

    @GetMapping("/info/{token}")
    public ResponseEntity<APIResponse> getUserInfo(@PathVariable String token) {
        JwtResponse jwtResponse = userService.getUserInfo(token);
        if (jwtResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.error(HttpStatus.NOT_FOUND.value(), "User not found"));
        }
        return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "User info retrieved", jwtResponse));
    }


    @PostMapping("/logout")
    public ResponseEntity<APIResponse> logoutUser(@RequestBody String token) {
        APIResponse success = authService.logout(token);
        if (success == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.error(HttpStatus.NOT_FOUND.value(), "User not found"));
        }
        return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "User logged out", success));
    }


}
