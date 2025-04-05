package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.model.ProductVariant;
import com.example.CapstoneProject.repository.ProductVariantRepository;
import com.example.CapstoneProject.request.ColorRequest;
import com.example.CapstoneProject.mapper.ColorMapper;
import com.example.CapstoneProject.model.Color;
import com.example.CapstoneProject.repository.ColorRepository;
import com.example.CapstoneProject.response.ColorResponse;
import com.example.CapstoneProject.service.Interface.IColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColorService implements IColorService {

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Override
    public List<ColorResponse> getAllColor() {
        List<Color> colors = colorRepository.findAll();
        return colors.stream().map(ColorMapper::toColorResponse).collect(Collectors.toList());
    }

    @Override
    public boolean addColor(ColorRequest request) {
        if (colorRepository.existsByColor(request.getColor())) {
            return false;
        }
        Color color = ColorMapper.toColor(request);
        colorRepository.save(color);
        return true;
    }
    @Override
    public boolean updateColor(String id, ColorRequest request) {
        Color color = colorRepository.findById(id).orElse(null);
        if (color == null) {
            return false;
        }
        color.setColor(request.getColor());
        color.setColorCode(request.getColorCode());
        colorRepository.save(color);
        return true;
    }
    @Override
    public boolean deleteColor(String id) {
        Color color = colorRepository.findById(id).orElse(null);
        if (color == null) {
            return false;
        }
        List<ProductVariant> variants = productVariantRepository.findByColorId(id);
        for (ProductVariant variant : variants) {
            variant.setStatus("UNAVAILABLE");
            productVariantRepository.save(variant);
        }
        color.setStatus("UNAVAILABLE");
        colorRepository.save(color);
        return true;
    }
}
