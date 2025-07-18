package com.example.CapstoneProject.controller.User;

import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.request.AddressRequest;
import com.example.CapstoneProject.request.FavoriteRequest;
import com.example.CapstoneProject.request.UserRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.JwtResponse;
import com.example.CapstoneProject.response.UserResponse;
import com.example.CapstoneProject.service.Interface.IAuthService;
import com.example.CapstoneProject.service.Interface.IDiscountCodeService;
import com.example.CapstoneProject.service.Interface.IFavoriteService;
import com.example.CapstoneProject.service.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IAuthService authService;

    @Autowired
    private IFavoriteService favoriteService;
    @Autowired
    private IDiscountCodeService discountCodeService;

    @GetMapping("/info/{token}")
    public ResponseEntity<APIResponse> getUserInfo(@PathVariable String token) {
        JwtResponse jwtResponse = userService.getUserInfo(token);
        if (jwtResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.error(HttpStatus.NOT_FOUND.value(), "User not found"));
        }
        return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "User info retrieved", jwtResponse));
    }
    @GetMapping("/get-all")
    public ResponseEntity<APIResponse> getAllDiscountCodes() {
        APIResponse response = discountCodeService.getAllDiscountCodes();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @PostMapping("/apply")
    public ResponseEntity<APIResponse> applyDiscountCodeToUser(
            @RequestParam String discountCode,
            @RequestParam String userId) {
        APIResponse response = discountCodeService.applyDiscountCodeToUser(discountCode, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
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
    @PutMapping("/info")
    public ResponseEntity<APIResponse> updateUserInfo(@RequestHeader("Authorization") String token,
                                                      @RequestParam(value = "fullName", required = false) String fullName,
                                                      @RequestParam(value = "avatar", required = false) MultipartFile avatar) {

        String newToken = token.substring(7);
        UserRequest userRequest = UserRequest.builder()
                .fullName(fullName)
                .avatar(avatar)
                .build();
        APIResponse response = userService.updateUserInfo(newToken, userRequest);
        if (response.getStatusCode() == Code.NOT_FOUND.getCode()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else if (response.getStatusCode() == Code.BAD_REQUEST.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }
    @PutMapping("/change-password")
    public ResponseEntity<APIResponse> changePassword(@RequestHeader("Authorization") String token,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        String newToken = token.substring(7);
        APIResponse response = authService.changePasswordByIdentifier(newToken, oldPassword, newPassword);
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
    @GetMapping("/discount-code")
    public ResponseEntity<APIResponse> getDiscountCodesByUser(@RequestHeader("Authorization") String token) {
        String newToken = token.substring(7);
        APIResponse response = discountCodeService.getDiscountCodesByUser(newToken);
        if (response.getStatusCode() == Code.NOT_FOUND.getCode()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }
    @PutMapping("/apply-discount-code")
    public ResponseEntity<APIResponse> applyDiscountCode(@RequestHeader("Authorization") String token,
            @RequestParam("discountCode") String discountCode) {
        String newToken = token.substring(7);
        APIResponse response = discountCodeService.applyDiscountCode(discountCode, newToken );
        if (response.getStatusCode() == Code.NOT_FOUND.getCode()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else if (response.getStatusCode() == Code.BAD_REQUEST.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }
}
