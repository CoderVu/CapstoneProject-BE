package com.example.CapstoneProject.mapper;

import com.example.CapstoneProject.request.ColorRequest;
import com.example.CapstoneProject.model.Color;
import com.example.CapstoneProject.response.ColorResponse;

public class ColorMapper {
    public static Color toColor(ColorRequest sizeRequest) {
       Color color = new Color();
       color.setColor(sizeRequest.getColor());
       color.setColorCode(sizeRequest.getColorCode());
       return color;
    }
    public static ColorResponse toColorResponse(Color color) {
        return ColorResponse.builder()
                .id(color.getColorId())
                .color(color.getColor())
                .colorCode(color.getColorCode())
                .build();
    }
}
