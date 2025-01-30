package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.Request.ProductRequest;
import com.example.CapstoneProject.Request.VariantRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.PaginatedResponse;
import com.example.CapstoneProject.response.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {
    boolean addProduct(ProductRequest request, List<MultipartFile> images);

    APIResponse updateVariant(String productId, String variantId, VariantRequest variantRequest);

    boolean deleteProduct(String id);

    PaginatedResponse<ProductResponse> getAllProduct(Pageable pageable);

    ProductResponse getProductById(String id);

    APIResponse addVariants(String productId, List<VariantRequest> variantRequests);

    PaginatedResponse<ProductResponse> getProductsByCollection(String collectionId, Pageable pageable);

    PaginatedResponse<ProductResponse> FilterProducts(Pageable pageable, String category, String brand, Double priceMin, Double priceMax, String color, String size);
}
