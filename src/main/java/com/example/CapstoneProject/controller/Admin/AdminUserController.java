package com.example.CapstoneProject.controller.Admin;

import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.UserResponse;
import com.example.CapstoneProject.service.Interface.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    @Autowired
    private IUserService userService;

    @GetMapping
    public ResponseEntity<APIResponse> getAllUsers() {
        List<UserResponse> users = userService.getAllUser();
        APIResponse response = new APIResponse(Code.OK.getCode(), "Fetched all users successfully", users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")

    public ResponseEntity<APIResponse> getUserById(@PathVariable String id) {
        UserResponse userResponse = userService.getUserInfoById(id);
        if (userResponse == null) {
            return ResponseEntity.status(Code.NOT_FOUND.getCode())
                    .body(new APIResponse(Code.NOT_FOUND.getCode(), "User not found", null));
        }
        APIResponse response = new APIResponse(Code.OK.getCode(), "Fetched user successfully", userResponse);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
 
    public ResponseEntity<APIResponse> deleteUser(@PathVariable String id) {
        APIResponse response = userService.deleteUserAccount(id);
        if (response.getStatusCode() == Code.NOT_FOUND.getCode()) {
            return ResponseEntity.status(Code.NOT_FOUND.getCode()).body(response);
        }
        return ResponseEntity.ok(response);
    }
}