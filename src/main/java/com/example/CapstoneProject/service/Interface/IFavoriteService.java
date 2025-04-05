package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.FavoriteRequest;
import com.example.CapstoneProject.response.APIResponse;

public interface IFavoriteService {
    APIResponse addFavorite(FavoriteRequest request);

    APIResponse removeFavorites(String token, String productId);

    APIResponse getAllFavorites(String token);
}
