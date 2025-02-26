package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.ProductDescriptionRequest;
import com.example.CapstoneProject.response.APIResponse;

public interface IProductDescriptionService {
    APIResponse addProductDescription(ProductDescriptionRequest request);

    APIResponse updateProductDescription(ProductDescriptionRequest request);

    APIResponse getProductDescription(String productId);
}
