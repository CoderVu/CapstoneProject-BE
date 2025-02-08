package com.example.CapstoneProject.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private String productName;
    private String description;
    private Integer price;
    private String categoryName;
    private Boolean onSale;
    private Boolean bestSeller;
    private String brandName;
    private Boolean newProduct;
}