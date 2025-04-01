package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.CategoryRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.CategoryResponse;

import java.util.List;

public interface ICategoryService {
    APIResponse addCategory(CategoryRequest request);

    APIResponse updateCategory(String id, CategoryRequest request);

    APIResponse deleteCategory(String id);

    List<CategoryResponse> getAllCategory();
}

