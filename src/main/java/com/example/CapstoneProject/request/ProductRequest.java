package com.example.CapstoneProject.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private String productName;
    private String description;
    private Double price;
    private Double discountPrice;
    private String categoryName;
    private String gender;
    private Boolean onSale;
    private Boolean bestSeller;
    private String brandName;
    private Boolean newProduct;
    private List<String> imageIds;
    private Map<String, List<String>> colorImages;
    private Map<String, String> colorMap;
    private String mainImageId;
}