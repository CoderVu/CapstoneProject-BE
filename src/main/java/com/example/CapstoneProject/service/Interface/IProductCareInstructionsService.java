package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.ProductCareInstructionsRequest;
import com.example.CapstoneProject.response.APIResponse;

public interface IProductCareInstructionsService {
     public APIResponse addProductCareInstructions(ProductCareInstructionsRequest request) ;
     public APIResponse getProductCareInstructions(String productId) ;
}
