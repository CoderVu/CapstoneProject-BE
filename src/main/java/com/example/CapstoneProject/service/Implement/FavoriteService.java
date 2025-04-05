package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.model.Product;
import com.example.CapstoneProject.model.User;
import com.example.CapstoneProject.repository.FavoriteProductRepository;
import com.example.CapstoneProject.repository.ProductRepository;
import com.example.CapstoneProject.repository.UserRepository;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.FavoriteProductResponse;
import com.example.CapstoneProject.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.CapstoneProject.request.FavoriteRequest;
import com.example.CapstoneProject.service.Interface.IFavoriteService;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService implements IFavoriteService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FavoriteProductRepository favoriteProductRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public APIResponse addFavorite(FavoriteRequest request) {
        String identifier = jwtUtils.getUserFromToken(request.getToken());
        Optional<User> user = Optional.empty();
        if (identifier != null) {
            user = userRepository.findByPhoneNumber(identifier);
            if (user.isEmpty()) {
                user = userRepository.findByEmail(identifier);
            }
        }
        if (user.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("User not found")
                    .build();
        }
        User foundUser = user.get();
        if (foundUser.getFavoriteProducts().stream()
                .anyMatch(product -> product.getId().equals(request.getProductId()))) {
            return APIResponse.builder()
                    .statusCode(Code.CONFLICT.getCode())
                    .message("Product already in favorites")
                    .build();
        }
        if (request.getProductId() == null || request.getProductId().isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.BAD_REQUEST.getCode())
                    .message("Product ID is required")
                    .build();
        }
        // Fetch the product from the repository
        Optional<Product> product = productRepository.findById(request.getProductId());
        if (product.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Product not found")
                    .build();
        }

        // Add the product to the user's favorites
        foundUser.getFavoriteProducts().add(product.get());
        userRepository.save(foundUser);

        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Thêm sản phẩm yêu thích thành công")
                .build();
    }
    @Override
    public APIResponse removeFavorites(String token, String productId) {
        String identifier = jwtUtils.getUserFromToken(token);
        Optional<User> user = Optional.empty();
        if (identifier != null) {
            user = userRepository.findByPhoneNumber(identifier);
            if (user.isEmpty()) {
                user = userRepository.findByEmail(identifier);
            }
        }
        if (user.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("User not found")
                    .build();
        }
        User foundUser = user.get();
        List<Product> favoriteProducts = foundUser.getFavoriteProducts();
        Product productToRemove = null;
        for (Product product : favoriteProducts) {
            if (product.getId().equals(productId)) {
                productToRemove = product;
                break;
            }
        }
        if (productToRemove != null) {
            favoriteProducts.remove(productToRemove);
            userRepository.save(foundUser);
            return APIResponse.builder()
                    .statusCode(Code.OK.getCode())
                    .message("Xóa sản phẩm yêu thích thành công")
                    .build();
        } else {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Product not found in favorites")
                    .build();
        }
    }
    @Override
    public APIResponse getAllFavorites(String token) {
        String identifier = jwtUtils.getUserFromToken(token);
        Optional<User> user = Optional.empty();
        if (identifier != null) {
            user = userRepository.findByPhoneNumber(identifier);
            if (user.isEmpty()) {
                user = userRepository.findByEmail(identifier);
            }
        }
        if (user.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("User not found")
                    .build();
        }
        User foundUser = user.get();
        List<FavoriteProductResponse> favoriteProducts = foundUser.getFavoriteProducts().stream()
                .map(product -> FavoriteProductResponse.builder()

                        .productId(product.getId())
                        .userId(foundUser.getId())
                        .build())
                .toList();
        return APIResponse.builder()
                .statusCode(Code.OK.getCode())
                .message("Lấy danh sách sản phẩm yêu thích thành công")
                .data(favoriteProducts)
                .build();
    }
}