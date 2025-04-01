package com.example.CapstoneProject.mapper;

import com.example.CapstoneProject.request.CategoryRequest;
import com.example.CapstoneProject.model.Category;
import com.example.CapstoneProject.response.CategoryResponse;


public class CategoryMapper {

    public static CategoryResponse toCategoryResponse(Category categories) {
        return CategoryResponse.builder()
                .id(categories.getId())
                .name(categories.getName())
                .imageUrl(categories.getImageUrl())
                .description(categories.getDescription())
                .build();
    }
}
