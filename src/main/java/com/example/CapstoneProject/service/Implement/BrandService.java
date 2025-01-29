package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.Request.BrandRequest;
import com.example.CapstoneProject.mapper.BrandMapper;
import com.example.CapstoneProject.model.Brand;
import com.example.CapstoneProject.repository.BrandRepository;
import com.example.CapstoneProject.service.Interface.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
