package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.request.BrandRequest;
import com.example.CapstoneProject.mapper.BrandMapper;
import com.example.CapstoneProject.model.Brand;
import com.example.CapstoneProject.repository.BrandRepository;
import com.example.CapstoneProject.response.BrandResponse;
import com.example.CapstoneProject.service.Interface.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService implements IBrandService {
    @Autowired
    private BrandRepository categoryRepository;

    @Override
    public boolean addBrand(BrandRequest request) {
        if (categoryRepository.findByName(request.getBrandName()) != null) {
            return false;
        }
        Brand brand = BrandMapper.toBrand(request);
        categoryRepository.save(brand);
        return true;
    }

    @Override
    public List<BrandResponse> getAllBrand() {
        List<Brand> brands = categoryRepository.findAll();
        return brands.stream().map(BrandMapper::toBrandResponse).collect(Collectors.toList());
    }

    @Override
    public boolean updateBrand(BrandRequest request) {
        Brand brand = categoryRepository.findById(request.getBrandId()).orElse(null);
        if (brand == null) {
            return false;
        }
        if (categoryRepository.findByName(request.getBrandName()) != null && !brand.getName().equals(request.getBrandName())) {
            return false;
        }
        brand.setName(request.getBrandName());
        categoryRepository.save(brand);
        return true;
    }

    @Override
    public boolean deleteBrand(String brandId) {
        Brand brand = categoryRepository.findById(brandId).orElse(null);
        if (brand == null) {
            return false;
        }
        categoryRepository.delete(brand);
        return true;

    }
}
