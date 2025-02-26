package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.ProductRequest;
import com.example.CapstoneProject.request.VariantRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.PaginatedResponse;
import com.example.CapstoneProject.response.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {

    APIResponse updateVariant(String productId, String variantId, VariantRequest variantRequest);

    boolean deleteProduct(String id);

    PaginatedResponse<ProductResponse> getAllProduct(Pageable pageable);

    PaginatedResponse<ProductResponse> getRelatedProducts(String productId, Pageable pageable);

    ProductResponse getProductById(String id);

    APIResponse addProduct(ProductRequest request, List<MultipartFile> images);

    APIResponse addVariants(String productId, List<VariantRequest> variantRequests);

    APIResponse getProductOnSale();

    PaginatedResponse<ProductResponse> getProductsByCollection(String collectionId, Pageable pageable);

    PaginatedResponse<ProductResponse> FilterProducts(Pageable pageable, String gender, String category, String brand, Double priceMin, Double priceMax, String color, String size);

    APIResponse updateProduct(String productId, ProductRequest productRequest, List<MultipartFile> imageFiles);
}
