package com.example.CapstoneProject.response;

import com.example.CapstoneProject.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private String id;
    private String userId;
    private String userName;
    private String avatar;
    private String productId;
    private String productName;
    private String image;
    private int quantity;
    private String statusQuantity;
    private Double unitPrice;
    private Double totalPrice;
    private String size;
    private String statusSize;
    private String color;
    private String statusColor;
    private String brand;
    private String categoryName;
    private String status;
    private boolean onSale;
    private boolean bestSeller;
    private boolean newProduct;
    private Double discountPrice;

}
