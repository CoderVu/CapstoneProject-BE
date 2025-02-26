package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.model.Product;
import com.example.CapstoneProject.model.ProductDescription;
import com.example.CapstoneProject.repository.ProductRepository;
import com.example.CapstoneProject.request.ProductDescriptionRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.ProductDescriptionResponse;
import com.example.CapstoneProject.service.Interface.IProductDescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.CapstoneProject.repository.ProductDescriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class ProductDescriptionService implements IProductDescriptionService {

    @Autowired
    private ProductDescriptionRepository productDescriptionRepository;
    @Autowired
    private ProductRepository productRepository;


    @Override
    public APIResponse addProductDescription(ProductDescriptionRequest request) {
        if (productDescriptionRepository.existsById(request.getProductId())) {
            return APIResponse.builder()
                    .data(null)
                    .message("Product description already exists")
                    .statusCode(404)
                    .build();
        }
        if (productDescriptionRepository.existsByProductId(request.getProductId())) {
            return APIResponse.builder()
                    .data(null)
                    .message("Product description already exists")
                    .statusCode(404)
                    .build();
        }
        Product product = productRepository.findById(request.getProductId()).orElse(null);
        if (product == null) {
            return APIResponse.builder()
                    .data(null)
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

        ProductDescription productDescription = new ProductDescription();
        productDescription.setProduct(product);
        productDescription.setAttributes(attributesJson);
        productDescription.setDescription(request.getDescription());
        productDescriptionRepository.save(productDescription);

        return APIResponse.builder()
                .message("Product description added successfully")
                .statusCode(200)
                .build();
    }
    @Override
    public APIResponse updateProductDescription(ProductDescriptionRequest request) {
        ProductDescription productDescription = productDescriptionRepository.findByProductId(request.getProductId());
        if (productDescription == null) {
            return APIResponse.builder()
                    .data(null)
                    .message("Product description not found")
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

        productDescription.setAttributes(attributesJson);
        productDescription.setDescription(request.getDescription());
        productDescriptionRepository.save(productDescription);

        return APIResponse.builder()
                .message("Product description updated successfully")
                .statusCode(200)
                .build();
    }

    @Override
    public APIResponse getProductDescription(String productId) {
        ProductDescription productDescription = productDescriptionRepository.findByProductId(productId);
        if (productDescription == null) {
            return APIResponse.builder()
                    .data(null)
                    .message("Product description not found")
                    .statusCode(404)
                    .build();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> attributes;
        try {
            attributes = objectMapper.readValue(productDescription.getAttributes(), Map.class);
        } catch (Exception e) {
            return APIResponse.builder()
                    .message("Error converting attributes to JSON")
                    .statusCode(500)
                    .build();
        }

        ProductDescriptionResponse response = ProductDescriptionResponse.builder()
                .productId(productDescription.getProduct().getId())
                .attributes(attributes)
                .description(productDescription.getDescription())
                .build();

        return APIResponse.builder()
                .data(response)
                .message("Product description retrieved successfully")
                .statusCode(200)
                .build();
    }
}