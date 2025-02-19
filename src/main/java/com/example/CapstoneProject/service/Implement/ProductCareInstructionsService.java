package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.model.CareInstructions;
import com.example.CapstoneProject.model.Product;
import com.example.CapstoneProject.model.ProductDescription;
import com.example.CapstoneProject.repository.CareInstructionsRepository;
import com.example.CapstoneProject.repository.ProductRepository;
import com.example.CapstoneProject.request.ProductCareInstructionsRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.ProductCareInstructionsResponse;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.CapstoneProject.service.Interface.IProductCareInstructionsService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductCareInstructionsService implements IProductCareInstructionsService {

    @Autowired
    private CareInstructionsRepository careInstructionsRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public APIResponse addProductCareInstructions(ProductCareInstructionsRequest request) {
        if (careInstructionsRepository.existsById(request.getProductId())) {
            return APIResponse.builder()
                    .message("Product care instructions already exists")
                    .statusCode(404)
                    .build();
        }
        if (careInstructionsRepository.existsByProductId(request.getProductId())) {
            return APIResponse.builder()
                    .message("Product care instructions already exists")
                    .statusCode(404)
                    .build();
        }
          Product product = productRepository.findById(request.getProductId()).orElse(null);
        if (product == null) {
            return APIResponse.builder()
                    .message("Product not found")
                    .statusCode(404)
                    .build();
        }
        
        ObjectMapper objectMapper = new ObjectMapper();
        String attributesJson;
        try {
            attributesJson = objectMapper.writeValueAsString(request.getAttributes());
        } catch (Exception e) {
            return APIResponse.builder()
                    .data(null)
                    .message("Error converting attributes to JSON")
                    .statusCode(500)
                    .build();
        }

        CareInstructions careInstructions = new CareInstructions();
       careInstructions.setProduct(product);
       careInstructions.setAttributes(attributesJson);
       careInstructions.setCareDetails(request.getDescription());
       careInstructionsRepository.save(careInstructions);
        return APIResponse.builder()
                .message("Product description added successfully")
                .statusCode(200)
                .build();
    }
    
    @Override
    public APIResponse getProductCareInstructions(String productId) {
        CareInstructions careInstructions = careInstructionsRepository.findByProductId(productId);
        if (careInstructions == null) {
            return APIResponse.builder()
                    .data(null)
                    .message("Product care instructions not found")
                    .statusCode(404)
                    .build();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> attributes;
        try {
            attributes = objectMapper.readValue(careInstructions.getAttributes(), Map.class);
        } catch (Exception e) {
            return APIResponse.builder()
                    .data(null)
                    .message("Error converting attributes to JSON")
                    .statusCode(500)
                    .build();
        }

        ProductCareInstructionsResponse response = ProductCareInstructionsResponse.builder()
                .productId(careInstructions.getProduct().getId())
                .attributes(attributes)
                .description(careInstructions.getCareDetails())
                .build();

        return APIResponse.builder()
                .data(response)
                .message("Product description retrieved successfully")
                .statusCode(200)
                .build();
    }
}
