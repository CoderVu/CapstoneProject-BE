package com.example.CapstoneProject.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDescriptionRequest {
    private String productId;
    private Map<String, String> attributes;
    private String description;

}
