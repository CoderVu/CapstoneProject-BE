package com.example.CapstoneProject.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private String id;
    private String productName;
    private String description;
    private Integer price;
    private List<VariantResponse> variants;
    private String categoryName;
    private String brandName;
    private Boolean newProduct;
    private List<CollectionResponse> collections;
    private List<ImageResponse> images;
    private ImageResponse mainImage;
}
