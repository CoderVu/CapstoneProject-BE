package com.example.CapstoneProject.controller.User;

import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.request.AddressRequest;
import com.example.CapstoneProject.request.FavoriteRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.JwtResponse;
import com.example.CapstoneProject.response.UserResponse;
import com.example.CapstoneProject.service.Interface.IAuthService;
import com.example.CapstoneProject.service.Interface.IFavoriteService;
import com.example.CapstoneProject.service.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IAuthService authService;

    @Autowired
    private IFavoriteService favoriteService;

    @GetMapping("/info/{token}")
    public ResponseEntity<APIResponse> getUserInfo(@PathVariable String token) {
        JwtResponse jwtResponse = userService.getUserInfo(token);
        if (jwtResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.error(HttpStatus.NOT_FOUND.value(), "User not found"));
        }
        return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "User info retrieved", jwtResponse));
    }

    @GetMapping("/address")
    public ResponseEntity<APIResponse> getUserInfoByToken(@RequestHeader("Authorization") String token) {
        String newToken = token.substring(7);
        UserResponse userResponse = userService.getUserInfoByToken(newToken);
        if (userResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.error(HttpStatus.NOT_FOUND.value(), "User not found"));
        }
        return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "User info retrieved", userResponse));
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<APIResponse> deleteAddress(@RequestHeader("Authorization") String token,
            @PathVariable Long addressId) {
        String newToken = token.substring(7);
        APIResponse response = userService.deleteAddress(newToken, addressId);
        if (response.getStatusCode() == Code.NOT_FOUND.getCode()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else if (response.getStatusCode() == Code.BAD_REQUEST.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
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

    @PostMapping("/favorite")
    public ResponseEntity<APIResponse> addFavorite(@RequestHeader("Authorization") String token,
            @RequestParam("productId") String productId) {
        String newToken = token.substring(7);
        FavoriteRequest request = new FavoriteRequest();
        request.setToken(newToken);
        request.setProductId(productId);
        APIResponse response = favoriteService.addFavorite(request);
        if (response.getStatusCode() == Code.NOT_FOUND.getCode()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else if (response.getStatusCode() == Code.CONFLICT.getCode()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else if (response.getStatusCode() == Code.BAD_REQUEST.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/favorite")
    public ResponseEntity<APIResponse> removeFavorite(@RequestHeader("Authorization") String token,
            @RequestParam("productId") String productId) {
        String newToken = token.substring(7);
        APIResponse response = favoriteService.removeFavorites(newToken, productId);
        if (response.getStatusCode() == Code.NOT_FOUND.getCode()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/favorite")
    public ResponseEntity<APIResponse> getAllFavorites(@RequestHeader("Authorization") String token) {
        String newToken = token.substring(7);
        APIResponse response = favoriteService.getAllFavorites(newToken);
        if (response.getStatusCode() == Code.NOT_FOUND.getCode()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/address")
    public ResponseEntity<APIResponse> updateAddress(@RequestHeader("Authorization") String token,
            @RequestBody AddressRequest addressRequest) {
        String newToken = token.substring(7);
        addressRequest.setToken(newToken);
        APIResponse response = userService.updateAddress(addressRequest);
        if (response.getStatusCode() == Code.NOT_FOUND.getCode()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else if (response.getStatusCode() == Code.BAD_REQUEST.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }
}
