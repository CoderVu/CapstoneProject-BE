package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.Request.BrandRequest;
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
}
