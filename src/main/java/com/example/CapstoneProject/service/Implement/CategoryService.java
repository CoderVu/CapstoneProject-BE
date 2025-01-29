package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.Request.CategoryRequest;
import com.example.CapstoneProject.mapper.CategoryMapper;
import com.example.CapstoneProject.model.Category;
import com.example.CapstoneProject.repository.CategoryRepository;
import com.example.CapstoneProject.response.CategoryResponse;
import com.example.CapstoneProject.service.Interface.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService implements ICategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public boolean addCategory(CategoryRequest request) {
        if (categoryRepository.findByName(request.getName()) != null) {
            return false;
        }
        Category category = CategoryMapper.toCategory(request);
        categoryRepository.save(category);
        return true;
    }

    @Override
    public List<CategoryResponse> getAllCategory() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(CategoryMapper::toCategoryResponse).collect(Collectors.toList());
    }


}