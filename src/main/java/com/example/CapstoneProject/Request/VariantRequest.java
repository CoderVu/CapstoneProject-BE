package com.example.CapstoneProject.Request;

import lombok.Data;

@Data
public class VariantRequest {
    private String id;
    private String sizeName;
    private String colorName;
    private Integer quantity;
    private Integer price;
}