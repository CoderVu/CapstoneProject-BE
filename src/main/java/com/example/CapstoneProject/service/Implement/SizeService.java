package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.model.Color;
import com.example.CapstoneProject.model.ProductVariant;
import com.example.CapstoneProject.repository.ProductVariantRepository;
import com.example.CapstoneProject.request.SizeRequest;
import com.example.CapstoneProject.mapper.SizeMapper;
import com.example.CapstoneProject.model.Size;
import com.example.CapstoneProject.repository.SizeRepository;
import com.example.CapstoneProject.response.SizeResponse;
import com.example.CapstoneProject.service.Interface.ISizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SizeService implements ISizeService {

    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;

@Override
public List<SizeResponse> getAllSizes() {
        List<Size> sizes = sizeRepository.findAll();
        return sizes.stream()
                .filter(size -> "AVAILABLE".equals(size.getStatusSize()))
                .map(size -> SizeResponse.builder()
                        .id(size.getSizeId())
                        .name(size.getName())
                        .statusSize(size.getStatusSize())
                        .build())
                .collect(Collectors.toList());
    }
    @Override
    public boolean addSize(SizeRequest request) {
        Size existingSize = sizeRepository.findByName(request.getName());
        if (existingSize != null) {
            existingSize.setStatusSize("AVAILABLE");
            sizeRepository.save(existingSize);
            return true;
        }
        Size size = new Size();
        size.setName(request.getName());
        size.setStatusSize("AVAILABLE");
        sizeRepository.save(size);
        return true;
    }
    @Override
    public boolean updateSize(String id, SizeRequest request) {
        Size size = sizeRepository.findById(id).orElse(null);
        if (size == null) {
            return false;
        }
        size.setName(request.getName());
        sizeRepository.save(size);
        return true;
    }
    @Override
    public boolean deleteSize(String id) {
        Size size = sizeRepository.findById(id).orElse(null);
        if (size == null) {
            return false;
        }
        List<ProductVariant> variants = productVariantRepository.findBySizeId(id);
        for (ProductVariant variant : variants) {
            variant.setStatus("UNAVAILABLE");
            productVariantRepository.save(variant);
        }
        size.setStatusSize("UNAVAILABLE");
        sizeRepository.save(size);
        return true;
    }
}