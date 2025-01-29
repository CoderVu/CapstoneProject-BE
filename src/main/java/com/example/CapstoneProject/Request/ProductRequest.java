package com.example.CapstoneProject.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private String productName;
    private String description;
    private Integer price;
    private String categoryName;
    private String brandName;
    private Boolean newProduct;
}