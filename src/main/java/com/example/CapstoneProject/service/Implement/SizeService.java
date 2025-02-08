package com.example.CapstoneProject.service.Implement;

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

    public List<SizeResponse> getAllSizes() {
        List<Size> sizes = sizeRepository.findAll();
        return sizes.stream()
                .map(size -> SizeResponse.builder()
                        .id(size.getSizeId())
                        .name(size.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public boolean addSize(SizeRequest request) {
        if (sizeRepository.findByName(request.getSize()) != null) {
            return false;
        }
        Size size = SizeMapper.toSize(request);
        sizeRepository.save(size);
        return true;
    }
}