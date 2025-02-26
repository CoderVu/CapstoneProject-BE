package com.example.CapstoneProject.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartRequest {
    private String token;
    private String productId;
    private String productName;
    private String image;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private String size;
    private String color;
    private String brand;
    private String categoryName;
    private String description;
    private boolean onSale;
    private boolean bestSeller;
    private boolean newProduct;
    private double discountPrice;
}
