package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.CartRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.CartResponse;
import com.example.CapstoneProject.response.PaginatedResponse;

public interface ICartService {
    APIResponse addToCart(CartRequest request);

    APIResponse updateCart(String token, String cartId, Integer quantity, String color, String size);

    APIResponse deleteCart(String token, String cartId);

    PaginatedResponse<CartResponse> getCart(String token, int page, int size);
}
