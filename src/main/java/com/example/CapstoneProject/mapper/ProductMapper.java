package com.example.CapstoneProject.mapper;

import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.model.*;
import com.example.CapstoneProject.repository.*;
import com.example.CapstoneProject.response.*;
import com.example.CapstoneProject.request.ProductRequest;
import com.example.CapstoneProject.request.VariantRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductMapper {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;


    public ImageResponse toImageResponse(Image image) {
        if (image == null) {
            return null;
        }
        return ImageResponse.builder()
                .id(image.getId())
                .path(image.getUrl())
                .color(image.getColor())
                .build();
    }
    public ProductResponse toProductResponse(Product product) {
        Double averageRate = 0.0;
        if (product.getRates() != null && !product.getRates().isEmpty()) {
            averageRate = product.getRates().stream()
                    .mapToDouble(Rate::getRate)
                    .average()
                    .orElse(0);
        }
        Integer totalRate = product.getRates() != null ? product.getRates().size() : 0;

        ProductRateResponse productRateResponse = ProductRateResponse.builder()
                .rating(averageRate)
                .totalRate(totalRate)
                .build();
        String type = "shose";
        // Calculate total quantity of all variants
        int totalQuantity = product.getVariants().stream()
                .filter(variant -> !"UNAVAILABLE".equals(variant.getColor().getStatus()))
                .mapToInt(ProductVariant::getQuantity)
                .sum();
        return ProductResponse.builder()
                .id(product.getId())
                .type(type)
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .gender(product.getGender())
                .onSale(product.getOnSale())
                .bestSeller(product.getBestSeller())
                .brandName(product.getBrand().getName())
                .categoryName(product.getCategory().getName())
                .newProduct(product.getNewProduct())
                .images(product.getImages().stream()
                        .map(this::toImageResponse)
                        .collect(Collectors.toList()))
                .mainImage(toImageResponse(product.getMainImage()))
                .variants(product.getVariants().stream()
                        .filter(variant -> !"UNAVAILABLE".equals(variant.getColor().getStatus()))
                        .map(variant -> VariantResponse.builder()
                                .id(variant.getId())
                                .sizeName(variant.getSize().getName())
                                .color(variant.getColor().getColor())
                                .quantity(variant.getQuantity())
                                .status(variant.getStatus())
                                .build())
                        .collect(Collectors.toList()))
                .quantity(totalQuantity)
                .rate(productRateResponse)
                .build();
    }
}