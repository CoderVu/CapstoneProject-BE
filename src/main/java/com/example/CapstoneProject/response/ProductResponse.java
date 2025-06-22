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
    private String type;
    private String productName;
    private String description;
    private Double price;
    private Double discountPrice;
    private String gender;
    private Boolean onSale;
    private List<String> collectionName;
    private Boolean bestSeller;
    private Integer sold;
    private List<VariantResponse> variants;
    private Integer quantity;
    private String categoryName;
    private String brandName;
    private Boolean newProduct;
    private List<ImageResponse> images;
    private ImageResponse mainImage;
    private ProductRateResponse rate;
    private String status;
}
