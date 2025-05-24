package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.ProductRequest;
import com.example.CapstoneProject.request.VariantRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.PaginatedResponse;
import com.example.CapstoneProject.response.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IProductService {


    APIResponse addVariant(String productId, List<VariantRequest> variantRequests);

    APIResponse updateVariant(String productId, VariantRequest variantRequest);

    APIResponse deleteVariant(String variantId);

    boolean deleteProduct(String id);

    PaginatedResponse<ProductResponse> getAllProduct(Pageable pageable);


    PaginatedResponse<ProductResponse> SearchProducts(Pageable pageable, String keyword);

    APIResponse updateProduct(String productId, ProductRequest productRequest, Map<String, MultipartFile[]> colorImages);

    String extractColorFromFileName(String fileName, Map<String, String> colorMap);

    MultipartFile[] appendToArray(MultipartFile[] array, MultipartFile file);

    PaginatedResponse<ProductResponse> getRelatedProducts(String productId, Pageable pageable);

    ProductResponse getProductById(String id);

    APIResponse addProduct(ProductRequest request, List<MultipartFile> images);

    APIResponse getProductOnSale();

    PaginatedResponse<ProductResponse> getProductsByCollection(String collectionId, Pageable pageable);

    PaginatedResponse<ProductResponse> getProductsByCollectionName(String collectionName, Pageable pageable);

    PaginatedResponse<ProductResponse> FilterProducts(Pageable pageable, String gender, String category, String brand, Double priceMin, Double priceMax, String color, String size);


    APIResponse getColorByProductId(String productId);

    APIResponse getAllImages();

    PaginatedResponse<ProductResponse> getProductByImgUrl(List<String> imgUrls, Pageable pageable);
}
