package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.ProductCareInstructionsRequest;
import com.example.CapstoneProject.response.APIResponse;

public interface IProductCareInstructionsService {
     APIResponse addProductCareInstructions(ProductCareInstructionsRequest request) ;
     APIResponse getProductCareInstructions(String productId) ;
     APIResponse updateProductCareInstructions(ProductCareInstructionsRequest request);
}
