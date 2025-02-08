package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.ColorRequest;
import com.example.CapstoneProject.response.ColorResponse;

import java.util.List;

public interface IColorService {
    List<ColorResponse> getAllColor();

    boolean addColor(ColorRequest request);
}
