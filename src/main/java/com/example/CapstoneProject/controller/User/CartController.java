package com.example.CapstoneProject.controller.User;

import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.request.CartRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.CartResponse;
import com.example.CapstoneProject.response.PaginatedResponse;
import com.example.CapstoneProject.service.Interface.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/cart")
public class CartController {
    @Autowired
    private ICartService cartService;

    @PostMapping("/add")
    public ResponseEntity<APIResponse> addToCart(
            @RequestHeader("Authorization") String token,
            @RequestBody CartRequest request) {
        String newToken = token.substring(7);
        request.setToken(newToken);
        APIResponse response = cartService.addToCart(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get")
    public ResponseEntity<APIResponse> getCartItems(@RequestHeader("Authorization") String token,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "30") int size) {
        String newToken = token.substring(7);
        PaginatedResponse<CartResponse> response = cartService.getCart(newToken, page, size);
        APIResponse apiResponse = new APIResponse(Code.OK.getCode(), Code.OK.getMessage(), response);
        return ResponseEntity.ok(apiResponse);
    }
    @PutMapping("/update/{cartId}")
        public ResponseEntity<APIResponse> updateQuantityCart(@RequestHeader("Authorization") String token,
                                                              @PathVariable String cartId,
                                                              @RequestParam(required = false) int quantity,
                                                              @RequestParam(required = false) String color,
                                                              @RequestParam(required = false) String size) {
        String newToken = token.substring(7);
        APIResponse response = cartService.updateCart(newToken, cartId, quantity, color, size);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @DeleteMapping("/delete/{cartId}")
    public ResponseEntity<APIResponse> deleteCartItems(@RequestHeader("Authorization") String token,
                                                       @PathVariable String cartId) {
        String newToken = token.substring(7);
        APIResponse response = cartService.deleteCart(newToken, cartId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
