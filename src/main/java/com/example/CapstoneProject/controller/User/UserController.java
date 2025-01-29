package com.example.CapstoneProject.controller.User;

import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.UserResponse;
import com.example.CapstoneProject.service.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/info/{token}")
    public ResponseEntity<APIResponse> getUserInfo(@PathVariable String token) {
        UserResponse userResponse = userService.getUserInfo(token);
        if (userResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.error(HttpStatus.NOT_FOUND.value(), "User not found"));
        }
        return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "User info retrieved", userResponse));
    }



}
