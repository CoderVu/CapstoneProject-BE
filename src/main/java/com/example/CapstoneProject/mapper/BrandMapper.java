package com.example.CapstoneProject.mapper;

import com.example.CapstoneProject.request.BrandRequest;
import com.example.CapstoneProject.model.Brand;
import com.example.CapstoneProject.response.BrandResponse;

public class BrandMapper {
    public static Brand toBrand(BrandRequest brandRequest) {
        Brand brand = new Brand();
        brand.setName(brandRequest.getBrandName());
        return brand;
    }

    public static BrandResponse toBrandResponse(Brand brand) {
        return BrandResponse.builder()
                .brandId(brand.getId())
                .brandName(brand.getName())
                .build();
    }
}
