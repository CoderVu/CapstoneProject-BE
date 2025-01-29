package com.example.CapstoneProject.mapper;

import com.example.CapstoneProject.Request.CategoryRequest;
import com.example.CapstoneProject.model.Category;
import com.example.CapstoneProject.response.CategoryResponse;

import java.util.List;


public class CategoryMapper {
    public static Category toCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        return category;
    }

    public static CategoryResponse toCategoryResponse(Category categories) {
        return CategoryResponse.builder()
                .id(categories.getId())
                .name(categories.getName())
                .build();
    }
}
