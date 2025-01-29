package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.Request.CategoryRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.CategoryResponse;

import java.util.List;

public interface ICategoryService {
    boolean addCategory(CategoryRequest request);
    List<CategoryResponse> getAllCategory();
}

