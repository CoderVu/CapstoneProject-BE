package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.Request.ColorRequest;
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
}