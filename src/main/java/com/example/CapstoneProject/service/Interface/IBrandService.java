package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.BrandRequest;
import com.example.CapstoneProject.response.BrandResponse;

import java.util.List;

public interface IBrandService {
    boolean addBrand(BrandRequest request);

    List<BrandResponse> getAllBrand();
}
