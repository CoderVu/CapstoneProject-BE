package com.example.CapstoneProject.mapper;

import com.example.CapstoneProject.Request.BrandRequest;
import com.example.CapstoneProject.model.Brand;

public class BrandMapper {
    public static Brand toBrand(BrandRequest brandRequest) {
        Brand brand = new Brand();
        brand.setName(brandRequest.getBrandName());
        return brand;
    }
}
