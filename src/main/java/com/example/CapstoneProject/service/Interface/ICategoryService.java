package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.CategoryRequest;
import com.example.CapstoneProject.response.CategoryResponse;

import java.util.List;

public interface ICategoryService {
    boolean addCategory(CategoryRequest request);
    List<CategoryResponse> getAllCategory();
}

